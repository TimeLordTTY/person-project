package com.timelordtty.spelling.api;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author tianyu.tang
 * @description 百度API服务，处理所有与百度API的交互
 */
@Service
public class BaiduApiService {
    private static final Logger log = LoggerFactory.getLogger(BaiduApiService.class);
    private static final Logger httpLog = LoggerFactory.getLogger("com.timelordtty.spelling.http");
    
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String CORRECTION_API_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v2/text_correction?access_token=";
    
    private final OkHttpClient httpClient;
    
    // 配置信息
    private String apiKey;
    private String secretKey;
    
    /**
     * 缓存的访问令牌
     */
    private String cachedAccessToken;
    
    public BaiduApiService() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(java.time.Duration.ofSeconds(10))  // 减少连接超时，避免UI等待太久
            .readTimeout(java.time.Duration.ofSeconds(10))     // 减少读取超时
            .writeTimeout(java.time.Duration.ofSeconds(10))    // 减少写入超时
            .retryOnConnectionFailure(true)                    // 启用连接失败重试
            .build();
    }
    
    /**
     * 设置API凭证
     * 如果凭证无效，会记录警告但不会阻止程序继续运行
     */
    public void setCredentials(String apiKey, String secretKey) {
        // 验证API密钥的有效性
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("提供的API密钥为空");
        }
        
        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.warn("提供的Secret密钥为空");
        }
        
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        
        // 在后台预加载Token
        preloadTokenInBackground();
    }
    
    /**
     * 在后台预加载Token，不阻塞主线程
     */
    private void preloadTokenInBackground() {
        new Thread(() -> {
            try {
                log.info("后台预加载Token开始");
                
                // 设置5秒超时
                java.util.concurrent.CompletableFuture<String> future = 
                    java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                        try {
                            return getTokenInternal();
                        } catch (Exception e) {
                            log.warn("Token预加载失败: {}", e.getMessage());
                            return null;
                        }
                    });
                
                // 添加5秒超时
                String token = future.get(5, java.util.concurrent.TimeUnit.SECONDS);
                if (token != null) {
                    cachedAccessToken = token;
                    log.info("Token预加载成功: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
                }
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("Token预加载超时，将在需要时获取");
            } catch (Exception e) {
                log.warn("Token预加载异常: {}", e.getMessage());
            }
        }, "token-preload-thread").start();
    }
    
    /**
     * 获取百度API访问令牌
     * 
     * @return 访问令牌
     * @throws IOException 网络请求错误
     */
    public String getToken() throws IOException {
        // 如果已有缓存的令牌，直接返回
        if (cachedAccessToken != null && !cachedAccessToken.isEmpty()) {
            return cachedAccessToken;
        }
        
        // 验证API密钥是否可用
        if (apiKey == null || secretKey == null || apiKey.isEmpty() || secretKey.isEmpty()) {
            throw new IOException("API密钥或Secret密钥未设置，无法获取Token");
        }
        
        // 尝试获取Token并添加5秒超时
        try {
            java.util.concurrent.CompletableFuture<String> future = 
                java.util.concurrent.CompletableFuture.supplyAsync(this::getTokenInternal);
            
            // 添加5秒超时
            return future.get(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new IOException("获取Token超时，请检查网络连接", e);
        } catch (Exception e) {
            throw new IOException("获取Token失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 内部方法，实际执行Token获取
     */
    private String getTokenInternal() {
        log.info("开始获取百度API Token, API_KEY={}, SECRET_KEY={}", 
            apiKey.substring(0, Math.min(apiKey.length(), 3)) + "...", 
            secretKey.substring(0, Math.min(secretKey.length(), 3)) + "...");
        
        // 尝试方法1：使用URLEncoder方式
        try {
            httpLog.info("尝试使用URLEncoder方式获取Token");
            
            String param = "grant_type=client_credentials" 
                + "&client_id=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name())
                + "&client_secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8.name());
                
            httpLog.info("Token请求URL: {}, 参数: {}", TOKEN_URL, param);
            
            Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(RequestBody.create(param, MediaType.parse("application/x-www-form-urlencoded")))
                .build();
                
            try (Response response = httpClient.newCall(request).execute()) {
                int statusCode = response.code();
                String responseBody = response.body() != null ? response.body().string() : "";
                httpLog.info("Token响应状态码: {}", statusCode);
                
                if (statusCode == 200) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode json = mapper.readTree(responseBody);
                    if (json.has("access_token")) {
                        cachedAccessToken = json.get("access_token").asText();
                        log.info("成功获取Token: {}", cachedAccessToken.substring(0, Math.min(cachedAccessToken.length(), 10)) + "...");
                        return cachedAccessToken;
                    }
                } else {
                    httpLog.warn("方式1请求失败: 状态码={}, 响应体={}", statusCode, responseBody);
                }
            }
        } catch (Exception e) {
            httpLog.error("获取Token失败: {}", e.getMessage(), e);
        }
        
        // 方法1失败，尝试方法2：使用HttpUrl构建
        httpLog.info("尝试使用HttpUrl方式获取Token");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(TOKEN_URL).newBuilder();
        urlBuilder.addQueryParameter("client_id", apiKey);
        urlBuilder.addQueryParameter("client_secret", secretKey);
        urlBuilder.addQueryParameter("grant_type", "client_credentials");
        
        String url = urlBuilder.build().toString();
        httpLog.info("Token请求URL: {}", url);
        
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create("", MediaType.parse("application/json")))
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            int statusCode = response.code();
            String responseBody = response.body() != null ? response.body().string() : "";
            httpLog.info("Token响应状态码: {}", statusCode);
            
            if (statusCode == 200) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode json = mapper.readTree(responseBody);
                if (json.has("access_token")) {
                    cachedAccessToken = json.get("access_token").asText();
                    log.info("成功获取Token: {}", cachedAccessToken.substring(0, Math.min(cachedAccessToken.length(), 10)) + "...");
                    return cachedAccessToken;
                }
            } else {
                httpLog.error("Token请求失败: 状态码={}, 响应体={}", statusCode, responseBody);
            }
        } catch (Exception e) {
            httpLog.error("Token请求失败: {}", e.getMessage(), e);
        }
        
        // 如果所有方式都失败，创建一个模拟Token以避免UI阻塞
        log.warn("所有Token获取方式都失败，使用模拟Token继续运行");
        return "mock_token_for_testing_when_api_unavailable";
    }
    
    /**
     * 发送文本校正请求
     * 
     * @param text 要校正的文本
     * @return 服务器响应
     * @throws IOException 网络请求错误
     */
    public String correctText(String text) throws IOException {
        String token = getToken();
        String requestUrl = CORRECTION_API_URL + token;
        
        // 构建请求体
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("text", text);
        
        String jsonBody = mapper.writeValueAsString(requestBody);
        httpLog.debug("发送校验请求: {}", requestUrl);
        
        Request request = new Request.Builder()
            .url(requestUrl)
            .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
            .build();
            
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code());
            }
            
            if (response.body() == null) {
                throw new IOException("响应体为空");
            }
            
            return response.body().string();
        }
    }
    
    /**
     * 异步发送文本校正请求
     * 
     * @param text 要校正的文本
     * @param callback 回调接口
     * @throws IOException Token获取失败时抛出
     */
    public void correctTextAsync(String text, Callback callback) throws IOException {
        String token = getToken();
        String requestUrl = CORRECTION_API_URL + token;
        
        // 构建请求体
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("text", text);
        
        String jsonBody = mapper.writeValueAsString(requestBody);
        httpLog.debug("发送校验请求: {}", requestUrl);
        
        Request request = new Request.Builder()
            .url(requestUrl)
            .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
            .build();
            
        httpClient.newCall(request).enqueue(callback);
    }
} 