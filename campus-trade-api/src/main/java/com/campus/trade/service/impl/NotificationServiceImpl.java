package com.campus.trade.service.impl;

import com.campus.trade.entity.Notification;
import com.campus.trade.mapper.NotificationMapper;
import com.campus.trade.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public void createNotification(String userId, String type, String content, String relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> getUserNotifications(String userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public long getUnreadNotificationCount(String userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void markNotificationAsRead(String notificationId, String userId) {
        // 在实际项目中，这里应该先校验该通知是否属于该用户
        notificationMapper.markAsRead(notificationId);
    }

    @Override
    public void markAllNotificationsAsRead(String userId) {
        notificationMapper.markAllAsReadByUserId(userId);
    }
}
