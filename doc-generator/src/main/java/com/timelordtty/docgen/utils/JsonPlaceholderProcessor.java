package com.timelordtty.docgen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * JSON占位符处理工具类
 * 用于处理嵌套的JSON数据结构中的占位符替换
 */
public class JsonPlaceholderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JsonPlaceholderProcessor.class);
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^{}]+)\\}");
    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("([^\\[\\]]+)\\[(\\d+)\\]");

    /**
     * 主方法用于测试
     */
    public static void main(String[] args) {
        try {
            // 加载示例JSON数据
            String jsonPath = "doc-generator/src/main/resources/templates/sample_data.json";
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dataMap;
            
            try (FileInputStream fis = new FileInputStream(new File(jsonPath))) {
                dataMap = mapper.readValue(fis, new TypeReference<Map<String, Object>>() {});
            }
            
            // 测试一些占位符
            String[] testPlaceholders = {
                "${reportInfo.title}",
                "${projectInfo.name}",
                "${projectInfo.status}",
                "${milestones[0].name}",
                "${milestones[2].completion}",
                "${resources[1].role}",
                "${risks[0].mitigationPlan}",
                "${summary.progressPercent}"
            };
            
            System.out.println("测试JsonPlaceholderProcessor的占位符替换功能:");
            System.out.println("=============================================");
            
            for (String placeholder : testPlaceholders) {
                String path = extractPath(placeholder);
                Object value = getValueByPath(dataMap, path);
                System.out.println(placeholder + " => " + (value != null ? value.toString() : "null"));
            }
            
            // 测试文本替换
            String testText = "项目名称: ${projectInfo.name}，当前状态: ${projectInfo.status}，完成度: ${summary.progressPercent}%";
            String processed = processPlaceholders(testText, dataMap);
            System.out.println("\n文本替换测试:");
            System.out.println("原文本: " + testText);
            System.out.println("替换后: " + processed);
            
        } catch (IOException e) {
            logger.error("测试占位符处理时发生错误", e);
            e.printStackTrace();
        }
    }

    /**
     * 查找并返回文本中包含的所有占位符
     * 
     * @param text 包含占位符的文本
     * @return 占位符列表
     */
    public static List<String> findPlaceholders(String text) {
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        
        while (matcher.find()) {
            placeholders.add(matcher.group(0)); // 完整的占位符，例如${reportInfo.title}
        }
        
        return placeholders;
    }
    
    /**
     * 从文本中解析占位符的路径部分
     * 
     * @param placeholder 完整的占位符，例如${reportInfo.title}
     * @return 路径部分，例如reportInfo.title
     */
    public static String extractPath(String placeholder) {
        if (placeholder.startsWith(PLACEHOLDER_PREFIX) && placeholder.endsWith(PLACEHOLDER_SUFFIX)) {
            return placeholder.substring(PLACEHOLDER_PREFIX.length(), placeholder.length() - PLACEHOLDER_SUFFIX.length());
        }
        return placeholder;
    }

    /**
     * 从数据Map中获取指定路径的值
     * 支持处理嵌套对象和数组索引
     * 
     * @param dataMap 数据Map
     * @param path 数据路径，例如"reportInfo.title"或"milestones[0].name"
     * @return 对应路径的值，如果路径不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public static Object getValueByPath(Map<String, Object> dataMap, String path) {
        if (path == null || path.isEmpty() || dataMap == null) {
            return null;
        }

        // 分割路径为各部分
        String[] pathParts = path.split("\\.");
        Object currentObject = dataMap;

        for (String part : pathParts) {
            // 检查当前对象是否为空
            if (currentObject == null) {
                return null;
            }

            // 处理数组索引的情况，如 milestones[0]
            Matcher arrayMatcher = ARRAY_INDEX_PATTERN.matcher(part);
            if (arrayMatcher.matches()) {
                String arrayName = arrayMatcher.group(1);
                int index = Integer.parseInt(arrayMatcher.group(2));

                // 确保当前对象是Map并且包含数组名称的键
                if (!(currentObject instanceof Map)) {
                    logger.warn("路径{}中的{}不是一个Map对象", path, part);
                    return null;
                }

                Map<String, Object> mapObject = (Map<String, Object>) currentObject;
                if (!mapObject.containsKey(arrayName)) {
                    logger.warn("数据中不存在键: {}", arrayName);
                    return null;
                }

                // 获取数组对象
                Object arrayObject = mapObject.get(arrayName);
                if (!(arrayObject instanceof List)) {
                    logger.warn("{}不是一个数组", arrayName);
                    return null;
                }

                List<Object> list = (List<Object>) arrayObject;
                if (index >= list.size()) {
                    logger.warn("数组索引{}超出范围，数组大小为{}", index, list.size());
                    return null;
                }

                // 更新当前对象为数组中的元素
                currentObject = list.get(index);
            } else {
                // 处理普通属性访问
                if (!(currentObject instanceof Map)) {
                    logger.warn("路径{}中的{}不是一个Map对象", path, part);
                    return null;
                }

                Map<String, Object> mapObject = (Map<String, Object>) currentObject;
                if (!mapObject.containsKey(part)) {
                    logger.warn("数据中不存在键: {}", part);
                    return null;
                }

                // 更新当前对象
                currentObject = mapObject.get(part);
            }
        }

        return currentObject;
    }

    /**
     * 处理文本中的占位符，将其替换为数据Map中的实际值
     * 
     * @param text 包含占位符的文本
     * @param dataMap 数据Map
     * @return 替换后的文本
     */
    public static String processPlaceholders(String text, Map<String, Object> dataMap) {
        if (text == null || text.isEmpty() || dataMap == null) {
            return text;
        }

        // 查找所有占位符
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(0); // 完整的占位符
            String path = extractPath(placeholder); // 提取路径部分
            
            // 获取对应路径的值
            Object value = getValueByPath(dataMap, path);
            String replacement = value != null ? value.toString() : "";
            
            // 替换占位符
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
} 