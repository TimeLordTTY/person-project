package com.person.web.dto;

import java.time.LocalDateTime;

/**
 * 用户资料数据传输对象
 * <p>
 * 该类用于在系统各层之间传递用户资料数据，同时作为API响应的数据结构。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
public class UserProfileDTO {
    
    /**
     * 用户资料ID，主键
     */
    private Long id;
    
    /**
     * Steam用户ID
     */
    private String steamId;
    
    /**
     * Steam用户名
     */
    private String steamUsername;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 在线状态
     */
    private String status;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 国家/地区
     */
    private String country;
    
    /**
     * 个人简介
     */
    private String summary;
    
    /**
     * 最后离线时间
     */
    private LocalDateTime lastLogoff;
    
    /**
     * 记录创建时间
     */
    private LocalDateTime created;
    
    /**
     * 记录更新时间
     */
    private LocalDateTime updated;
    
    // 构造函数
    public UserProfileDTO() {
    }
    
    public UserProfileDTO(Long id, String steamId, String steamUsername, String avatarUrl, String status,
                        String realName, String country, String summary, LocalDateTime lastLogoff,
                        LocalDateTime created, LocalDateTime updated) {
        this.id = id;
        this.steamId = steamId;
        this.steamUsername = steamUsername;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.realName = realName;
        this.country = country;
        this.summary = summary;
        this.lastLogoff = lastLogoff;
        this.created = created;
        this.updated = updated;
    }
    
    // Getter 方法
    public Long getId() {
        return id;
    }
    
    public String getSteamId() {
        return steamId;
    }
    
    public String getSteamUsername() {
        return steamUsername;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public String getCountry() {
        return country;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public LocalDateTime getLastLogoff() {
        return lastLogoff;
    }
    
    public LocalDateTime getCreated() {
        return created;
    }
    
    public LocalDateTime getUpdated() {
        return updated;
    }
    
    // Setter 方法
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }
    
    public void setSteamUsername(String steamUsername) {
        this.steamUsername = steamUsername;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public void setLastLogoff(LocalDateTime lastLogoff) {
        this.lastLogoff = lastLogoff;
    }
    
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
    
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
} 