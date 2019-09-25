package com.ly.xunwu.entity;

import com.ly.xunwu.XunwuApplicationTests;
import com.ly.xunwu.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserRepositoryTest extends XunwuApplicationTests {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testFindOne() {
       User user = userRepository.findOne(1L);

        Assert.assertEquals("wali", user.getName());
    }
}
