package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.entity.User;
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * 【修改】增加 page 和 size 参数，用于接收前端的分页请求
     */
    @GetMapping
    public Result<PageResult<User>> getAllUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<User> userPage = userService.findAllUsers(page, size);
        return Result.success(userPage);
    }

    @PutMapping("/{id}/status")
    public Result<User> updateUserStatus(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUserStatus(id, user.getStatus());
        return Result.success(updatedUser);
    }
}