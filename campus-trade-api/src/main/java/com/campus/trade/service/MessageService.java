package com.campus.trade.service;

import com.campus.trade.dto.ConversationDTO; // 【新增】
import com.campus.trade.dto.MessageDTO;
import com.campus.trade.entity.Message;
import java.util.List;

public interface MessageService {
    Message sendMessage(String senderId, MessageDTO messageDTO);
    List<Message> getMessageHistory(String userId1, String userId2);

    // 【新增】
    List<ConversationDTO> getConversations(String userId);
}
