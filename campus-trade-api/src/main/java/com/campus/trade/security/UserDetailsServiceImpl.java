package com.campus.trade.security;

import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 【新增】导入
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections; // 【新增】导入

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 【关键修改】将用户的角色(如 "ADMIN") 转换为Spring Security的权限对象
        // 我们在角色名前加上 "ROLE_" 前缀，这是Spring Security的约定
        return new AuthenticatedUser(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())), // 传递角色权限
                user.getId(),
                user.getNickname()
        );
    }
}