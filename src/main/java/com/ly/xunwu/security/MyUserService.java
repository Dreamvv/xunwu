package com.ly.xunwu.security;

import com.ly.xunwu.entity.User;
import com.ly.xunwu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author liyong
 * @date 2019/9/5
 */
public class MyUserService implements UserDetailsService {

    @Autowired
    private IUserService userService;
    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findUserByName(s);

        return user;
    }
}
