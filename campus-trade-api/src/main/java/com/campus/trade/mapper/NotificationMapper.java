package com.campus.trade.mapper;

import com.campus.trade.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper {
    void insert(Notification notification);
    List<Notification> findByUserId(String userId);
    long countUnreadByUserId(String userId);
    void markAsRead(String notificationId);
    void markAllAsReadByUserId(String userId);
}