package com.person.web.service;

import com.person.web.dto.UserProfileDTO;

/**
 * Steam用户资料服务接口
 * <p>
 * 该接口定义了与Steam用户资料相关的服务操作，包括获取用户资料和刷新用户资料。
 * 实现类负责与Steam API交互，获取用户数据并转换为应用可用的格式。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
public interface SteamService {
    
    /**
     * 获取用户资料
     * <p>
     * 从数据库获取已存储的用户资料，如果不存在则调用刷新方法从Steam API获取
     * </p>
     * 
     * @return 用户资料DTO对象
     * @author tianyu.tang
     */
    UserProfileDTO getUserProfile();
    
    /**
     * 刷新用户资料
     * <p>
     * 从Steam API获取最新的用户资料，更新数据库，并返回更新后的资料
     * </p>
     * 
     * @return 更新后的用户资料DTO对象
     * @author tianyu.tang
     */
    UserProfileDTO refreshUserProfile();
} 