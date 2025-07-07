package com.campus.trade.dto;

import com.campus.trade.entity.User;

// 这个 DTO (数据传输对象) 只包含我们希望公开展示的用户信息
public class UserProfileDTO {
    private String id;
    private String nickname;
    private String avatar;
    private Integer creditScore;
    private String username; // 学号

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
        this.creditScore = user.getCreditScore();
        this.username = user.getUsername();
    }

    // Getters
    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getAvatar() { return avatar; }
    public Integer getCreditScore() { return creditScore; }
    public String getUsername() { return username; }
}