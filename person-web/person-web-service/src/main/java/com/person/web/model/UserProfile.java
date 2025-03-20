package com.person.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

/**
 * 用户资料实体类
 * <p>
 * 该实体类映射到数据库中的用户资料表，存储Steam用户的基本信息。
 * 使用JPA注解进行对象关系映射，使用Lombok注解简化代码。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
@Entity
public class UserProfile {
    
    /**
     * 主键ID，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 用户状态（在线/离线等）
     */
    private String status;
    
    /**
     * 用户真实姓名
     */
    private String realName;
    
    /**
     * 用户所在国家/地区
     */
    private String country;
    
    /**
     * 用户个人简介
     */
    private String summary;
    
    /**
     * 用户最后离线时间
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
    public UserProfile() {
    }

    public UserProfile(Long id, String steamId, String steamUsername, String avatarUrl, String status, 
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