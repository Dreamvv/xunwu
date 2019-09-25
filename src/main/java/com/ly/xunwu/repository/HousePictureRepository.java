package com.ly.xunwu.repository;

import com.ly.xunwu.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author liyong
 * @date 2019/9/6
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {

    List<HousePicture> findAllByHouseId(Long id);
}
