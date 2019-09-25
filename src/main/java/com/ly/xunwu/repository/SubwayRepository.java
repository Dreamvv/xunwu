package com.ly.xunwu.repository;

import com.ly.xunwu.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author liyong
 * @date 2019/9/6
 */
public interface SubwayRepository extends CrudRepository<Subway, Long> {
    List<Subway> findAllByCityEnName(String cityEnName);
}
