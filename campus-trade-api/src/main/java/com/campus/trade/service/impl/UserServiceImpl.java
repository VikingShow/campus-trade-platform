package com.campus.trade.service.impl;

import com.campus.trade.dto.*;
import com.campus.trade.entity.User;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.security.JwtUtil;
import com.campus.trade.service.UserService;
import com.campus.trade.service.impl.EmailServiceImpl; // 【新增】
import org.springframework.data.redis.core.StringRedisTemplate; // 【新增】

import com.github.pagehelper.Page; // 【新增】
import com.github.pagehelper.PageHelper; // 【新增】
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate; // 【新增】


    @Autowired
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, StringRedisTemplate redisTemplate) { //【修改】构造函数
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate; // 【新增】
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
            throw new CustomException("该学号已被注册");
        }
        if (userMapper.findByEmail(registerDTO.getEmail()) != null) {
            throw new CustomException("该邮箱已被注册");
        }

        // 【新增】校验验证码
        String redisKey = EmailServiceImpl.VERIFICATION_CODE_KEY_PREFIX + registerDTO.getEmail();
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new CustomException("验证码已过期，请重新发送");
        }
        if (!storedCode.equals(registerDTO.getVerificationCode())) {
            throw new CustomException("验证码错误");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getNickname());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setEmailVerified(true); // 验证通过

        userMapper.insertUser(user);

        // 注册成功后，删除验证码
        redisTemplate.delete(redisKey);
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

        // 【修改】登录成功后，返回更完整的用户信息
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("id", authenticatedUser.getUserId());
        response.put("username", authenticatedUser.getUsername()); // 返回学号
        response.put("nickname", authenticatedUser.getNickname());
        response.put("avatar", authenticatedUser.getAvatar());
        // 注意：我们从数据库完整的User对象中获取bio
        User fullUser = userMapper.findById(authenticatedUser.getUserId());
        response.put("bio", fullUser.getBio());
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
        userToUpdate.setBio(profileDTO.getBio()); // 更新简介
        userMapper.updateProfile(userToUpdate);
        return userMapper.findById(userId);
    }

    @Override
    public PageResult<User> findAllUsers(String keyword, String role, Integer status, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        // 【修改】将所有筛选参数传递给 Mapper
        List<User> userList = userMapper.findAll(keyword, role, status);
        return new PageResult<>(userList);
    }

    @Override
    @Transactional
    public User createUserByAdmin(AdminUserDTO userDTO) {
        if (userMapper.findByUsername(userDTO.getUsername()) != null) {
            throw new CustomException("用户名（学号）已存在");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setNickname(userDTO.getNickname());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setStatus(1); // 默认启用
        userMapper.insertUser(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUserByAdmin(String userId, AdminUserDTO userDTO) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        user.setNickname(userDTO.getNickname());
        user.setRole(userDTO.getRole());
        user.setCreditScore(userDTO.getCreditScore());
        // 如果提供了新密码，则更新密码
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userMapper.updateUserByAdmin(user);
        return user;
    }

    @Override
    public void deleteUser(String userId, String currentAdminId) {
        if (Objects.equals(userId, currentAdminId)) {
            throw new CustomException("不能删除自己");
        }
        userMapper.deleteById(userId);
    }
}
