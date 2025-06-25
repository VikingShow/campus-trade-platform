package com.campus.trade.service.impl;

import com.campus.trade.dto.ConversationDTO;
import com.campus.trade.dto.MessageDTO;
import com.campus.trade.entity.Message;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Autowired
    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public Message sendMessage(String senderId, MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(messageDTO.getReceiverId());
        message.setProductId(messageDTO.getProductId());
        message.setContent(messageDTO.getContent());

        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Message> getMessageHistory(String userId1, String userId2) {
        return messageMapper.findMessagesBetweenUsers(userId1, userId2);
    }

    @Override
    public List<ConversationDTO> getConversations(String userId) {
        return messageMapper.findConversationsByUserId(userId);
    }
}