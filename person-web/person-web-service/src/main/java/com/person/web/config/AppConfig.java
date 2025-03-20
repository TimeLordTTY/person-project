package com.person.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用程序配置类
 * <p>
 * 该配置类提供全局应用程序设置，包括CORS配置和RestTemplate的创建。
 * 实现WebMvcConfigurer接口以自定义Spring MVC的配置。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /**
     * 配置跨域资源共享(CORS)策略
     * <p>
     * 允许前端应用访问后端API，设置允许的来源、方法和头信息
     * </p>
     * 
     * @param registry CORS注册表
     * @author tianyu.tang
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("配置CORS策略");
        registry.addMapping("/**")
                .allowedOrigins("*")  // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);  // 当allowedOrigins为*时，必须设置为false
        logger.debug("CORS策略已配置: 允许所有来源");
    }

    /**
     * 创建RestTemplate Bean
     * <p>
     * RestTemplate用于调用外部API，例如Steam API
     * </p>
     * 
     * @return 配置好的RestTemplate实例
     * @author tianyu.tang
     */
    @Bean
    public RestTemplate restTemplate() {
        logger.info("创建RestTemplate Bean");
        return new RestTemplate();
    }
} 