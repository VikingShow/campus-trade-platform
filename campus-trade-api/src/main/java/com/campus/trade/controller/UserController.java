package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.LoginDTO;
import com.campus.trade.dto.RegisterDTO;
import com.campus.trade.dto.UserProfileDTO; // 【新增】导入新的 DTO
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/authenticate")
    public Result<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, String> result = userService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 【新增】一个公开的、用于获取用户主页信息的接口
     * 这个接口不需要任何权限即可访问
     */
    @GetMapping("/{id}/profile")
    public Result<UserProfileDTO> getUserProfile(@PathVariable String id) {
        UserProfileDTO userProfile = userService.findUserPublicProfile(id);
        return Result.success(userProfile);
    }
}