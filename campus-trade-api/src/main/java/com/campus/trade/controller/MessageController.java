package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.ConversationDTO;
import com.campus.trade.dto.MessageDTO;
import com.campus.trade.entity.Message;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@PreAuthorize("isAuthenticated()") // 整个Controller都需要登录后才能访问
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Result<Message> sendMessage(@RequestBody MessageDTO messageDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        Message sentMessage = messageService.sendMessage(user.getUserId(), messageDTO);
        return Result.success(sentMessage);
    }

    @GetMapping("/history/{otherUserId}")
    public Result<List<Message>> getHistory(@PathVariable String otherUserId, @AuthenticationPrincipal AuthenticatedUser user) {
        List<Message> history = messageService.getMessageHistory(user.getUserId(), otherUserId);
        return Result.success(history);
    }

    @GetMapping("/conversations")
    public Result<List<ConversationDTO>> getConversations(@AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(messageService.getConversations(user.getUserId()));
    }
}
