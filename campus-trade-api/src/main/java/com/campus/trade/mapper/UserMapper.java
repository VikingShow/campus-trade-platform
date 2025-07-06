package com.campus.trade.mapper;

import com.campus.trade.dto.DailyStatsDTO;
import com.campus.trade.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    void insertUser(User user);

    // 【新增】管理员用的Mapper方法
    List<User> findAll(@Param("keyword") String keyword, @Param("role") String role, @Param("status") Integer status);
    void updateUserStatus(@Param("id") String id, @Param("status") Integer status);
    User findById(String id);
    User findByEmail(String email); // 【新增】

    // 【新增】更新用户信誉分的方法
    void updateCreditScore(@Param("userId") String userId, @Param("scoreChange") int scoreChange);

    void updateProfile(User user);

    long countTotalUsers();
    List<DailyStatsDTO> countDailyRegistrations();
    List<DailyStatsDTO> countUserGrowth(@Param("days") int days);

    void updateUserByAdmin(User user);
    void deleteById(String id);
}