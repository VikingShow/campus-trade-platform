package com.campus.trade.service;

import com.campus.trade.dto.LoginDTO;
import com.campus.trade.dto.RegisterDTO;
import com.campus.trade.dto.UserProfileDTO; // 【新增】
import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.dto.UserProfileUpdateDTO;
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

    PageResult<User> findAllUsers(Integer page, Integer size);
}