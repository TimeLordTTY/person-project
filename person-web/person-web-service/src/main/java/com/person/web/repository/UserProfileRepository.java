package com.person.web.repository;

import com.person.web.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户资料仓库接口
 * <p>
 * 该接口继承自JpaRepository，提供与UserProfile实体相关的数据库操作功能。
 * Spring Data JPA会自动生成该接口的实现类，无需手动编写SQL语句。
 * </p>
 * 
 * @author tianyu.tang
 * @version 1.0.0
 * @since 2025-03-19
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * 根据Steam ID查找用户资料
     * <p>
     * 该方法使用Spring Data的命名约定自动生成查询语句，
     * 等同于"SELECT * FROM user_profile WHERE steam_id = ?"
     * </p>
     * 
     * @param steamId Steam用户ID
     * @return 用户资料实体，如果不存在则返回null
     * @author tianyu.tang
     */
    UserProfile findBySteamId(String steamId);
} 