package com.campus.trade.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

// 这个类现在也需要携带角色信息
public class AuthenticatedUser extends User {
    private final String userId;
    private final String nickname;
    private final String avatar; // 【新增】


    public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userId, String nickname, String avatar) { //【修改】
        super(username, password, authorities);
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar; // 【新增】

    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() { return avatar; } // 【新增】

}