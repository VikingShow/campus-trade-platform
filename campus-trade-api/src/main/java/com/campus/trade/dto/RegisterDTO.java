package com.campus.trade.dto;

// 【关键修正】移除了 @Data 注解，并手动添加了标准的 Getter 和 Setter 方法
public class RegisterDTO {

    private String username;
    private String nickname;
    private String password;

    // --- Getters ---
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    // --- Setters ---
    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
