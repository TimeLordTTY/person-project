package com.person.web.service.impl;

import com.person.web.dto.UserProfileDTO;
import com.person.web.model.UserProfile;
import com.person.web.repository.UserProfileRepository;
import com.person.web.service.SteamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Steam用户资料服务实现类
 * <p>
 * 该类实现了SteamService接口，提供与Steam用户资料相关的服务操作。
 * 负责从数据库或Steam API获取用户资料数据，并进行必要的转换。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 * @see SteamService
 */
@Service
public class SteamServiceImpl implements SteamService {

    private static final Logger logger = LoggerFactory.getLogger(SteamServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 用户资料仓库，用于数据库操作
     */
    private final UserProfileRepository userProfileRepository;
    
    /**
     * RestTemplate，用于调用Steam API
     */
    private final RestTemplate restTemplate;

    /**
     * Steam API密钥，从配置文件中注入
     */
    @Value("${steam.api-key}")
    private String steamApiKey;

    /**
     * Steam用户ID，从配置文件中注入
     */
    @Value("${steam.user-id}")
    private String steamUserId;
    
    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param userProfileRepository 用户资料仓库
     * @param restTemplate REST请求模板
     * @author tianyu.tang
     */
    public SteamServiceImpl(UserProfileRepository userProfileRepository, RestTemplate restTemplate) {
        logger.info("初始化SteamService...");
        this.userProfileRepository = userProfileRepository;
        this.restTemplate = restTemplate;
        logger.debug("SteamService初始化完成，依赖注入成功");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfileDTO getUserProfile() {
        logger.info("获取用户资料，steamId: {}", steamUserId);
        UserProfile profile = userProfileRepository.findBySteamId(steamUserId);
        
        if (profile == null) {
            logger.info("数据库中不存在用户资料，将从Steam API获取");
            return refreshUserProfile();
        }
        
        logger.debug("从数据库获取到用户资料: username={}, lastUpdated={}", 
                profile.getSteamUsername(), 
                profile.getUpdated() != null ? profile.getUpdated().format(DATE_FORMATTER) : "未知");
        
        return convertToDTO(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfileDTO refreshUserProfile() {
        logger.info("刷新用户资料，steamId: {}", steamUserId);
        
        try {
            // 记录API调用开始时间
            long startTime = System.currentTimeMillis();
            
            logger.debug("准备调用Steam API，apiKey={} (前3位)", 
                    steamApiKey != null && steamApiKey.length() > 3 ? steamApiKey.substring(0, 3) + "..." : "未设置");
            
            // 实际应用中，这里会调用Steam API获取数据
            // 由于这是演示，我们创建一个模拟数据
            UserProfile profile = userProfileRepository.findBySteamId(steamUserId);
            if (profile == null) {
                logger.info("创建新的用户资料记录");
                profile = new UserProfile();
                profile.setSteamId(steamUserId);
                profile.setCreated(LocalDateTime.now());
            } else {
                logger.info("更新现有用户资料记录，id={}", profile.getId());
            }

            // 设置模拟数据
            profile.setSteamUsername("SteamUser");
            profile.setAvatarUrl("https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/default.jpg");
            profile.setStatus("在线");
            profile.setRealName("Steam用户");
            profile.setCountry("中国");
            profile.setSummary("这是一个Steam用户资料");
            profile.setLastLogoff(LocalDateTime.now());
            profile.setUpdated(LocalDateTime.now());

            // 保存到数据库
            logger.debug("保存用户资料到数据库");
            profile = userProfileRepository.save(profile);
            logger.info("用户资料刷新成功，耗时: {}ms", System.currentTimeMillis() - startTime);
            
            // 转换为DTO并返回
            return convertToDTO(profile);
        } catch (Exception e) {
            logger.error("刷新用户资料失败", e);
            throw new RuntimeException("刷新用户资料失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将用户资料实体转换为DTO
     * 
     * @param profile 用户资料实体
     * @return 用户资料DTO
     * @author tianyu.tang
     */
    private UserProfileDTO convertToDTO(UserProfile profile) {
        logger.debug("转换UserProfile实体为DTO");
        UserProfileDTO dto = new UserProfileDTO();
        BeanUtils.copyProperties(profile, dto);
        logger.trace("DTO转换完成: {}", dto);
        return dto;
    }
} 