package com.campus.trade.service.impl;

import com.campus.trade.dto.LoginDTO;
import com.campus.trade.dto.RegisterDTO;
import com.campus.trade.dto.UserProfileDTO;
import com.campus.trade.dto.UserProfileUpdateDTO;
import com.campus.trade.entity.User;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.security.JwtUtil;
import com.campus.trade.service.UserService;
import com.campus.trade.dto.PageResult; // 【新增】
import com.github.pagehelper.Page; // 【新增】
import com.github.pagehelper.PageHelper; // 【新增】
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new CustomException("用户名（学号）已被注册");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getNickname());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        userMapper.insertUser(user);
    }

    @Override
    public Map<String, String> login(LoginDTO loginDTO) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        } catch (AuthenticationException e) {
            throw new CustomException("用户名或密码错误");
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        final String token = jwtUtil.generateToken(authenticatedUser);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("nickname", authenticatedUser.getNickname());
        response.put("id", authenticatedUser.getUserId());
        response.put("avatar", authenticatedUser.getAvatar()); // 【新增】在响应中加入头像
        return response;
    }


    @Override
    public User updateUserStatus(String userId, Integer status) {
        userMapper.updateUserStatus(userId, status);
        return userMapper.findById(userId);
    }

    @Override
    public UserProfileDTO findUserPublicProfile(String userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        // 返回一个只包含公开信息的 DTO 对象，而不是完整的 User 实体
        return new UserProfileDTO(user);
    }

    @Override
    public User updateUserProfile(String userId, UserProfileUpdateDTO profileDTO) {
        User userToUpdate = userMapper.findById(userId);
        if (userToUpdate == null) {
            throw new CustomException("用户不存在");
        }
        userToUpdate.setNickname(profileDTO.getNickname());
        userToUpdate.setAvatar(profileDTO.getAvatar());
        userMapper.updateProfile(userToUpdate);
        // 返回更新后的完整用户信息
        return userMapper.findById(userId);
    }

    @Override
    public PageResult<User> findAllUsers(Integer page, Integer size) {
        // 1. 设置分页参数
        PageHelper.startPage(page, size);
        // 2. 正常执行查询，PageHelper会自动拦截这条SQL，并为其附加分页逻辑
        Page<User> userPage = (Page<User>) userMapper.findAll();
        // 3. 将查询结果封装成我们自定义的 PageResult 对象
        return new PageResult<>(userPage);
    }
}
