package com.ly.xunwu.security;

import com.ly.xunwu.entity.User;
import com.ly.xunwu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * @author liyong
 * @date 2019/9/4
 */

public class AuthProvider implements AuthenticationProvider{
    @Autowired
    private IUserService userService;
    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();
        System.out.println("username----------------"+userName);
        User user = userService.findUserByName(userName);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }

        if (this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId())) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        }

        throw new BadCredentialsException("authError");

    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
