package com.campus.trade.dto;

// 这个 DTO 用于管理员创建或更新用户时，接收前端传来的数据
public class AdminUserDTO {
    private String username;
    private String nickname;
    private String password; // 创建用户或重置密码时使用
    private String role;
    private Integer status;
    private Integer creditScore;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
}