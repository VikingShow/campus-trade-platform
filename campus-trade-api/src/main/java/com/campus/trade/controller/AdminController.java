package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // 【新增】导入权限注解
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')") // 【关键】保护整个Controller，只有ADMIN角色能访问
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.findAllUsers());
    }

    @PutMapping("/{id}/status")
    public Result<User> updateUserStatus(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUserStatus(id, user.getStatus());
        return Result.success(updatedUser);
    }
}