package com.campus.trade.service;

import com.campus.trade.entity.Notification;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, String type, String content, String relatedId);
    List<Notification> getUserNotifications(String userId);
    long getUnreadNotificationCount(String userId);
    void markNotificationAsRead(@Param("notificationId") String notificationId, @Param("userId") String userId);
    void markAllNotificationsAsRead(String userId);
}