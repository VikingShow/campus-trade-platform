package com.campus.trade.dto;

// 【关键修正】移除了 @Data 注解，并手动添加了标准的 Getter 和 Setter 方法
public class LoginDTO {

    private String username;
    private String password;

    // --- Getters ---
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // --- Setters ---
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
