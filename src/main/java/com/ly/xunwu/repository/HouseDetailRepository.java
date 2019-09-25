package com.ly.xunwu.repository;

import com.ly.xunwu.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author liyong
 * @date 2019/9/6
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail,Long> {

    HouseDetail findByHouseId(Long houseId);

    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);
}
