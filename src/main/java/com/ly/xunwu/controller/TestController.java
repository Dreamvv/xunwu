package com.ly.xunwu.controller;

import com.ly.xunwu.entity.User;
import com.ly.xunwu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;
//
//@RestController
//public class TestController {
//    @Resource
//    private UserRepository userRepository;
//
//    @GetMapping("/test")
//    public User find(){
//        Optional<User> user = userRepository.findById(1L);
//        return user.get();
//    }
//}
