package com.campus.trade.service;

import com.campus.trade.dto.*;
import com.campus.trade.entity.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    void register(RegisterDTO registerDTO);
    Map<String, String> login(LoginDTO loginDTO);
    User updateUserStatus(String userId, Integer status);

    // 【新增】获取用户公开信息的方法声明
    UserProfileDTO findUserPublicProfile(String userId);
    User updateUserProfile(String userId, UserProfileUpdateDTO profileDTO);

    PageResult<User> findAllUsers(String keyword, String role, Integer status, Integer page, Integer size);
    User createUserByAdmin(AdminUserDTO userDTO);
    User updateUserByAdmin(String userId, AdminUserDTO userDTO);
    void deleteUser(String userId, String currentAdminId);

}