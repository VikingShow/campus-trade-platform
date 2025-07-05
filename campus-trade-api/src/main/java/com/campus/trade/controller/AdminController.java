package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.AdminUserDTO; // 【新增】
import com.campus.trade.dto.PageResult;
import com.campus.trade.entity.User;
import com.campus.trade.security.AuthenticatedUser; // 【新增】
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 【新增】
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<PageResult<User>> getAllUsers(
            // 【修改】增加 role 和 status 请求参数
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.findAllUsers(keyword, role, status, page, size));
    }

    @PostMapping
    public Result<User> createUser(@RequestBody AdminUserDTO userDTO) {
        return Result.success(userService.createUserByAdmin(userDTO));
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable String id, @RequestBody AdminUserDTO userDTO) {
        return Result.success(userService.updateUserByAdmin(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id, @AuthenticationPrincipal AuthenticatedUser admin) {
        userService.deleteUser(id, admin.getUserId());
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<User> updateUserStatus(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUserStatus(id, user.getStatus());
        return Result.success(updatedUser);
    }
}
