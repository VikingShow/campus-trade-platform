package com.campus.trade.mapper;

import com.campus.trade.dto.ConversationDTO; // 【新增】
import com.campus.trade.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageMapper {
    void insert(Message message);
    List<Message> findMessagesBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    // 【新增】获取用户所有会话列表的查询
    List<ConversationDTO> findConversationsByUserId(String userId);
}