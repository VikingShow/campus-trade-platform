package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.Notification;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Result<List<Notification>> getMyNotifications(@AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(notificationService.getUserNotifications(user.getUserId()));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal AuthenticatedUser user) {
        long count = notificationService.getUnreadNotificationCount(user.getUserId());
        return Result.success(Map.of("count", count));
    }

    @PostMapping("/mark-all-as-read")
    public Result<Void> markAllAsRead(@AuthenticationPrincipal AuthenticatedUser user) {
        notificationService.markAllNotificationsAsRead(user.getUserId());
        return Result.success();
    }
}