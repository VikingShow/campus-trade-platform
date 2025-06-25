package com.campus.trade.dto;

// 这个 DTO (数据传输对象) 只携带允许用户修改的字段
public class UserProfileUpdateDTO {
    private String nickname;
    private String avatar;

    // Getters and Setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
