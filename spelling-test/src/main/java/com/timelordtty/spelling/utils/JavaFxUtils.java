package com.timelordtty.spelling.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * @author tianyu.tang
 * @description JavaFX相关工具类，处理JavaFX运行环境配置
 */
public final class JavaFxUtils {
    private static final Logger log = LoggerFactory.getLogger(JavaFxUtils.class);
    
    private JavaFxUtils() {
        // 工具类禁止实例化
    }
    
    /**
     * 检测当前运行环境
     * @return true表示EXE/JAR环境，false表示IDE/开发环境
     */
    public static boolean detectRunningEnvironment() {
        // 通过路径判断是否为EXE环境
        String classpath = System.getProperty("java.class.path", "");
        
        // EXE环境通常不包含大量classpath条目
        boolean isExeEnv = !classpath.contains(";") || classpath.contains("all.jar");
        
        // 如果存在logback-exe.xml，也可能是EXE/JAR环境
        File logbackExe = new File("logback-exe.xml");
        if (logbackExe.exists()) {
            isExeEnv = true;
        }
        
        return isExeEnv;
    }
    
    /**
     * 设置JavaFX模块路径
     * @return 设置成功返回true，失败返回false
     */
    public static boolean setupJavaFxModulePath() {
        try {
            // 首先尝试检查系统属性中是否已经设置了模块路径
            String existingModulePath = System.getProperty("jdk.module.path");
            if (existingModulePath != null && !existingModulePath.isEmpty()) {
                log.info("使用已设置的模块路径: {}", existingModulePath);
                return true;
            }
            
            // 尝试从Maven仓库查找JavaFX模块
            String userHome = System.getProperty("user.home");
            String[] possiblePaths = {
                "target/dependency",
                userHome + "/.m2/repository/org/openjfx",
                "spelling-test/target/dependency",
                // 添加更多可能的路径
                "lib/javafx-modules",
                "javafx-sdk/lib"
            };
            
            log.debug("正在检查可能的JavaFX目录");
            
            for (String path : possiblePaths) {
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory()) {
                    log.info("找到JavaFX模块目录: {}", dir.getAbsolutePath());
                    
                    String modulePath = findJavaFxJars(dir);
                    if (modulePath != null && !modulePath.isEmpty()) {
                        // 设置系统属性
                        System.setProperty("javafx.module.path", modulePath);
                        
                        // 设置命令行JavaFX参数
                        // 这对于未来版本可能是必要的
                        System.setProperty("jdk.module.path", modulePath);
                        
                        // 设置JavaFX modules参数
                        System.setProperty("javafx.modules", "javafx.controls,javafx.fxml,javafx.base,javafx.graphics");
                        
                        log.info("已设置JavaFX模块路径: {}", modulePath);
                        return true;
                    }
                }
            }
            
            // 尝试添加添加模块
            log.warn("无法找到JavaFX模块，尝试手动设置模块");
            
            // 如果找不到，尝试设置一个默认值或使用VM参数
            String fallbackModulePath = System.getProperty("user.dir") + "/target/dependency";
            System.setProperty("javafx.module.path", fallbackModulePath);
            
            // 记录详细的环境信息，帮助调试
            log.info("当前工作目录: {}", System.getProperty("user.dir"));
            log.info("Java版本: {}", System.getProperty("java.version"));
            log.info("JavaFX模块回退路径: {}", fallbackModulePath);
            
            return false;
        } catch (Exception e) {
            log.error("设置JavaFX模块路径失败", e);
            return false;
        }
    }
    
    /**
     * 在给定目录中查找JavaFX JAR文件
     * @param dir 要搜索的目录
     * @return 找到的JavaFX JAR路径，用路径分隔符连接
     */
    public static String findJavaFxJars(File dir) {
        StringBuilder paths = new StringBuilder();
        
        // 检查当前目录中的JAR
        File[] jars = dir.listFiles((d, name) -> 
            name.endsWith(".jar") && (
                name.contains("javafx-base") || 
                name.contains("javafx-controls") || 
                name.contains("javafx-fxml") || 
                name.contains("javafx-graphics")
            )
        );
        
        if (jars != null && jars.length > 0) {
            for (File jar : jars) {
                if (paths.length() > 0) {
                    paths.append(File.pathSeparator);
                }
                paths.append(jar.getAbsolutePath());
            }
            return paths.toString();
        }
        
        // 如果当前目录没有找到，递归搜索子目录
        File[] subdirs = dir.listFiles(File::isDirectory);
        if (subdirs != null) {
            for (File subdir : subdirs) {
                String result = findJavaFxJars(subdir);
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 检查并创建日志目录
     */
    public static void checkAndCreateLogDir() {
        // 尝试多种可能的位置
        File[] possibleLogDirs = {
            new File("logs"),                   // 根目录
            new File("spelling-test/logs")      // 子目录
        };
        
        for (File logsDir : possibleLogDirs) {
            if (!logsDir.exists()) {
                log.info("尝试创建日志目录: {}", logsDir.getAbsolutePath());
                if (logsDir.mkdirs()) {
                    log.info("日志目录创建成功: {}", logsDir.getAbsolutePath());
                    break;
                } else {
                    log.warn("日志目录创建失败，尝试下一个位置");
                }
            } else {
                log.info("找到已存在的日志目录: {}", logsDir.getAbsolutePath());
                break;
            }
        }
    }
    
    /**
     * 查找logback配置文件
     * @return 找到的配置文件，未找到则返回null
     */
    public static File findLogbackConfig() {
        // 尝试多种可能的位置
        File[] possibleLocations = {
            new File("logback-exe.xml"),                    // 根目录
            new File("spelling-test/logback-exe.xml"),      // 子目录
            new File("target/logback-exe.xml"),             // 根目录target
            new File("spelling-test/target/logback-exe.xml")// 子目录target
        };
        
        for (File file : possibleLocations) {
            if (file.exists()) {
                log.info("找到配置文件: {}", file.getAbsolutePath());
                return file;
            }
        }
        
        log.info("未找到外部日志配置，使用内置配置");
        return null;
    }
} 