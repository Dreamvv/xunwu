package com.ly.xunwu.repository;

import com.ly.xunwu.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author liyong
 * @date 2019/9/6
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
    HouseTag findByNameAndHouseId(String name, Long houseId);

    List<HouseTag> findAllByHouseId(Long id);

    List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);
}
