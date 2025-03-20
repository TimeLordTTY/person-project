package com.person.web.controller;

import com.person.web.dto.UserProfileDTO;
import com.person.web.service.SteamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户资料控制器
 * <p>
 * 该控制器提供用户资料相关的REST API端点，包括获取用户资料和刷新用户资料。
 * 所有端点都映射到"/profile"路径下。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    /**
     * Steam用户服务
     */
    private final SteamService steamService;
    
    /**
     * 构造函数，通过依赖注入初始化控制器
     * 
     * @param steamService Steam用户服务
     * @author tianyu.tang
     */
    public ProfileController(SteamService steamService) {
        logger.info("初始化ProfileController...");
        this.steamService = steamService;
    }

    /**
     * 获取用户资料API
     * <p>
     * HTTP GET请求: /api/profile
     * </p>
     * 
     * @return 包含用户资料的HTTP响应
     * @author tianyu.tang
     */
    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile() {
        logger.info("收到获取用户资料请求");
        try {
            UserProfileDTO profile = steamService.getUserProfile();
            logger.info("用户资料获取成功: steamId={}", profile.getSteamId());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("获取用户资料时发生错误", e);
            throw e;
        }
    }

    /**
     * 刷新用户资料API
     * <p>
     * HTTP POST请求: /api/profile/refresh
     * </p>
     * 
     * @return 包含更新后用户资料的HTTP响应
     * @author tianyu.tang
     */
    @PostMapping("/refresh")
    public ResponseEntity<UserProfileDTO> refreshProfile() {
        logger.info("收到刷新用户资料请求");
        try {
            long startTime = System.currentTimeMillis();
            UserProfileDTO profile = steamService.refreshUserProfile();
            logger.info("用户资料刷新成功: steamId={}, 耗时={}ms", 
                    profile.getSteamId(), 
                    System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("刷新用户资料时发生错误", e);
            throw e;
        }
    }
} 