package com.timelordtty.spelling.config;

import com.timelordtty.spelling.api.BaiduApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.*;
import java.util.Properties;

/**
 * @author tianyu.tang
 * @description API配置类，负责初始化API服务
 */
@Configuration
public class ApiConfig {
    private static final Logger log = LoggerFactory.getLogger(ApiConfig.class);
    
    @Value("${baidu.api.key:}")
    private String apiKey;
    
    @Value("${baidu.api.secret:}")
    private String secretKey;
    
    private final Environment environment;
    
    public ApiConfig(Environment environment) {
        this.environment = environment;
    }
    
    @Bean
    public BaiduApiService baiduApiService() {
        BaiduApiService apiService = new BaiduApiService();
        
        // 优先使用配置文件中的值
        String configApiKey = environment.getProperty("baidu.api.key");
        String configSecretKey = environment.getProperty("baidu.api.secret");
        
        if (isValidApiKey(configApiKey) && isValidApiKey(configSecretKey)) {
            log.info("从Spring环境中加载API密钥");
            apiService.setCredentials(configApiKey, configSecretKey);
            return apiService;
        }
        
        // 尝试从备用位置加载
        log.warn("从Spring环境中未能加载API密钥，尝试备用方式加载");
        if (loadApiCredentialsFromBackup(apiService)) {
            return apiService;
        }
        
        // 使用注入的默认值（如果有）
        if (isValidApiKey(apiKey) && isValidApiKey(secretKey)) {
            log.info("使用注入的默认API密钥");
            apiService.setCredentials(apiKey, secretKey);
            return apiService;
        }
        
        log.error("无法加载API密钥，API功能将不可用");
        return apiService;
    }
    
    /**
     * 验证API密钥是否有效
     */
    private boolean isValidApiKey(String key) {
        return key != null && !key.isEmpty() && !key.equals("${baidu.api.key}") && !key.equals("${baidu.api.secret}");
    }
    
    /**
     * 从备用位置加载API凭证
     */
    private boolean loadApiCredentialsFromBackup(BaiduApiService apiService) {
        log.info("尝试从备用位置加载配置");
        
        // 可能的配置文件位置
        String[] configFiles = {
            "config.properties",
            "./config.properties",
            System.getProperty("user.home") + "/spelling-test/config.properties"
        };
        
        // 尝试每个位置
        for (String configFile : configFiles) {
            Properties props = loadPropertiesFile(configFile);
            if (props != null) {
                String propsApiKey = props.getProperty("baidu.api.key");
                String propsSecretKey = props.getProperty("baidu.api.secret");
                
                if (isValidApiKey(propsApiKey) && isValidApiKey(propsSecretKey)) {
                    log.info("从{}加载API_KEY: 成功", configFile);
                    log.info("从{}加载SECRET_KEY: 成功", configFile);
                    apiService.setCredentials(propsApiKey, propsSecretKey);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 加载Properties文件
     */
    private Properties loadPropertiesFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            log.info("配置文件不存在或无法读取: {}", filePath);
            return null;
        }
        
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            props.load(input);
            log.info("成功加载配置文件: {}", filePath);
            return props;
        } catch (IOException e) {
            log.error("读取配置文件失败: {}", e.getMessage());
            return null;
        }
    }
} 