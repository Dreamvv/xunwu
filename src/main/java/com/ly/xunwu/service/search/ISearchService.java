package com.ly.xunwu.service.search;

import com.ly.xunwu.dto.ServiceMultiResult;
import com.ly.xunwu.from.RentSearch;

/**
 * @author liyong
 * @date 2019/9/10
 */
public interface ISearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<Long> query(RentSearch rentSearch);
}
