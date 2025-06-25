package com.campus.trade.dto;

import java.util.Date;

// 这个 DTO 用于封装一个会话的预览信息
public class ConversationDTO {
    private String otherUserId;
    private String otherUserNickname;
    private String otherUserAvatar;
    private String lastMessageContent;
    private Date lastMessageCreateTime;

    // Getters and Setters
    public String getOtherUserId() { return otherUserId; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }
    public String getOtherUserNickname() { return otherUserNickname; }
    public void setOtherUserNickname(String otherUserNickname) { this.otherUserNickname = otherUserNickname; }
    public String getOtherUserAvatar() { return otherUserAvatar; }
    public void setOtherUserAvatar(String otherUserAvatar) { this.otherUserAvatar = otherUserAvatar; }
    public String getLastMessageContent() { return lastMessageContent; }
    public void setLastMessageContent(String lastMessageContent) { this.lastMessageContent = lastMessageContent; }
    public Date getLastMessageCreateTime() { return lastMessageCreateTime; }
    public void setLastMessageCreateTime(Date lastMessageCreateTime) { this.lastMessageCreateTime = lastMessageCreateTime; }
}