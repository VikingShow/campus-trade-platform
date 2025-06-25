package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.UserProfileUpdateDTO;
import com.campus.trade.entity.User;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me") // "me" 代表当前登录的用户
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public Result<User> updateUserProfile(@RequestBody UserProfileUpdateDTO profileDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        User updatedUser = userService.updateUserProfile(user.getUserId(), profileDTO);
        return Result.success(updatedUser);
    }
}
