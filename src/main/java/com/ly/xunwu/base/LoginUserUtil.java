package com.ly.xunwu.base;

import com.ly.xunwu.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author liyong
 * @date 2019/9/6
 */
public class LoginUserUtil {

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if (user == null) {
            return -1L;
        }

        return user.getId();
    }
}
