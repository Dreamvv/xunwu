package com.ly.xunwu.service;

import com.ly.xunwu.dto.ServiceResult;
import com.ly.xunwu.dto.UserDTO;
import com.ly.xunwu.entity.User;
/**
 * @author liyong
 * @date 2019/9/4
 */
public interface IUserService {

    User findUserByName(String userName);

    ServiceResult<UserDTO> findById(Long userId);
}
