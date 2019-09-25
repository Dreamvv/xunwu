package com.ly.xunwu.repository;

import com.ly.xunwu.entity.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User,Long> {

    User findByName(String userName);
}
