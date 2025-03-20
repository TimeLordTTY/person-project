package com.person.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Steam个人资料网站后端服务启动类
 * <p>
 * 该类是Spring Boot应用程序的入口点，负责启动整个后端服务。
 * 它使用Spring Boot的自动配置功能，自动扫描并注册所有在com.person.web包及其子包下的组件。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
@SpringBootApplication
public class PersonWebServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(PersonWebServiceApplication.class);
    
    /**
     * 应用程序入口点
     * <p>
     * 启动Spring Boot应用程序，并记录启动过程中的关键信息
     * </p>
     * 
     * @param args 命令行参数
     * @author tianyu.tang
     */
    public static void main(String[] args) {
        log.info("准备启动Steam个人资料网站后端服务...");
        
        try {
            // 记录JVM相关信息
            log.info("JDK版本: {}", System.getProperty("java.version"));
            log.info("JDK供应商: {}", System.getProperty("java.vendor"));
            log.info("JDK路径: {}", System.getProperty("java.home"));
            log.info("操作系统: {} {}", System.getProperty("os.name"), System.getProperty("os.arch"));
            
            // 启动SpringBoot应用
            ConfigurableApplicationContext applicationContext = SpringApplication.run(PersonWebServiceApplication.class, args);
            
            // 获取环境信息并记录到日志
            Environment env = applicationContext.getEnvironment();
            String port = env.getProperty("server.port");
            String contextPath = env.getProperty("server.servlet.context-path");
            
            log.info("----------------------------------------");
            log.info("Steam个人资料网站后端服务启动成功!");
            log.info("访问地址: http://localhost:{}{}", port, contextPath);
            log.info("数据库: {}", env.getProperty("spring.datasource.url"));
            log.info("服务名称: {}", env.getProperty("spring.application.name"));
            log.info("活动配置文件: {}", String.join(", ", env.getActiveProfiles()));
            log.info("----------------------------------------");
        } catch (Exception e) {
            log.error("应用程序启动失败!", e);
            throw e;
        }
    }
} 