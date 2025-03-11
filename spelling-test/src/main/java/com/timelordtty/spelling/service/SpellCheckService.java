package com.timelordtty.spelling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timelordtty.spelling.api.BaiduApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tianyu.tang
 * @description 拼写检查服务，处理文本校正业务逻辑
 */
@Service
public class SpellCheckService {
    private static final Logger log = LoggerFactory.getLogger(SpellCheckService.class);
    
    private final BaiduApiService apiService;
    private final ObjectMapper objectMapper;
    
    public SpellCheckService(BaiduApiService apiService) {
        this.apiService = apiService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 校验文本内容
     * 
     * @param text 要校验的文本
     * @return JSON格式的校验结果
     * @throws IOException 网络请求错误
     */
    public String checkText(String text) throws IOException {
        log.debug("开始校验文本，长度: {}", text.length());
        return apiService.correctText(text);
    }
    
    /**
     * 解析校验结果并获取校正后的文本
     * 
     * @param jsonResult API返回的JSON结果
     * @return 校正后的文本
     */
    public String getCorrectedText(String jsonResult) {
        try {
            JsonNode root = objectMapper.readTree(jsonResult);
            
            // 检查API响应是否正确
            if (root == null || !root.has("log_id")) {
                log.error("API响应格式错误，无效的JSON格式");
                return null;
            }
            
            // 检查是否有错误码
            if (root.has("error_code")) {
                int errorCode = root.get("error_code").asInt();
                String errorMsg = root.has("error_msg") ? root.get("error_msg").asText() : "未知错误";
                log.error("API返回错误: code={}, msg={}", errorCode, errorMsg);
                return null;
            }
            
            // 获取原始文本和纠错结果
            if (!root.has("text") || !root.has("item")) {
                log.error("API响应缺少必要字段: text或item");
                return null;
            }
            
            String originalText = root.get("text").asText();
            JsonNode items = root.get("item");
            
            // 处理校正结果
            if (items.isEmpty()) {
                log.info("文本没有错误需要校正");
                return originalText;
            }
            
            // 构建校正后的文本
            return buildCorrectedText(originalText, items);
        } catch (Exception e) {
            log.error("解析校验结果失败", e);
            return null;
        }
    }
    
    /**
     * 解析API结果，提取错误项列表
     * 
     * @param jsonResult API返回的JSON结果
     * @return 错误项列表，每项包含错误文本、推荐文本和位置信息
     */
    public List<ErrorItem> getErrorItems(String jsonResult) {
        List<ErrorItem> errorItems = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResult);
            
            // 检查API响应是否有效
            if (root == null || !root.has("item") || !root.has("text")) {
                return errorItems;
            }
            
            String originalText = root.get("text").asText();
            JsonNode items = root.get("item");
            
            // 解析每个错误项
            for (JsonNode item : items) {
                // 提取错误信息
                if (!item.has("loc") || !item.has("ori") || !item.has("correct")) {
                    continue;
                }
                
                String original = item.get("ori").asText();
                String corrected = item.get("correct").asText();
                
                // 获取位置
                JsonNode loc = item.get("loc");
                if (!loc.isObject() || !loc.has("offset")) {
                    continue;
                }
                
                int offset = loc.get("offset").asInt();
                
                // 创建错误项
                ErrorItem errorItem = new ErrorItem();
                errorItem.setOriginal(original);
                errorItem.setCorrected(corrected);
                errorItem.setOffset(offset);
                errorItem.setPosition(getLineCol(offset, originalText));
                
                errorItems.add(errorItem);
            }
        } catch (Exception e) {
            log.error("解析错误项失败", e);
        }
        
        return errorItems;
    }
    
    /**
     * 基于原文和错误项列表构建校正后的文本
     * 
     * @param originalText 原始文本
     * @param errorItems API返回的错误项JSON数组
     * @return 校正后的文本
     */
    private String buildCorrectedText(String originalText, JsonNode errorItems) {
        if (errorItems == null || errorItems.isEmpty()) {
            return originalText;
        }
        
        StringBuilder result = new StringBuilder(originalText);
        int offset = 0; // 用于跟踪替换后的位置偏移
        
        // 遍历所有错误项
        for (JsonNode item : errorItems) {
            if (!item.has("loc") || !item.has("ori") || !item.has("correct")) {
                continue;
            }
            
            // 获取原文和校正文本
            String original = item.get("ori").asText();
            String corrected = item.get("correct").asText();
            
            // 获取原文位置
            JsonNode loc = item.get("loc");
            if (!loc.isObject() || !loc.has("offset")) {
                continue;
            }
            
            int position = loc.get("offset").asInt() + offset;
            
            // 检查位置有效性
            if (position < 0 || position >= result.length()) {
                log.warn("无效的替换位置: {}, 文本长度: {}", position, result.length());
                continue;
            }
            
            // 执行替换
            try {
                result.replace(position, position + original.length(), corrected);
                // 更新偏移量（考虑校正文本与原文本长度差异）
                offset += (corrected.length() - original.length());
            } catch (Exception e) {
                log.error("替换文本失败: position={}, original={}, corrected={}", 
                    position, original, corrected, e);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 根据偏移量计算文本中的行列位置
     * 
     * @param offset 字符偏移量
     * @param text 源文本
     * @return 格式化的行列信息（行号:列号）
     */
    private String getLineCol(int offset, String text) {
        if (offset < 0 || offset >= text.length()) {
            return "位置错误";
        }
        
        int line = 1;
        int col = 1;
        
        for (int i = 0; i < offset; i++) {
            if (text.charAt(i) == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
        }
        
        return line + ":" + col;
    }
    
    /**
     * 错误项信息类
     */
    public static class ErrorItem {
        private String original;    // 原始错误文本
        private String corrected;   // 校正后文本
        private int offset;         // 在原文中的偏移量
        private String position;    // 格式化的位置信息 (行:列)
        
        public String getOriginal() {
            return original;
        }
        
        public void setOriginal(String original) {
            this.original = original;
        }
        
        public String getCorrected() {
            return corrected;
        }
        
        public void setCorrected(String corrected) {
            this.corrected = corrected;
        }
        
        public int getOffset() {
            return offset;
        }
        
        public void setOffset(int offset) {
            this.offset = offset;
        }
        
        public String getPosition() {
            return position;
        }
        
        public void setPosition(String position) {
            this.position = position;
        }
        
        @Override
        public String toString() {
            return "错误: '" + original + "' → '" + corrected + "' 位置: " + position;
        }
    }
} 