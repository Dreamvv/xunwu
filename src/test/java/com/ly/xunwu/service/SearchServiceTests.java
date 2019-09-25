package com.ly.xunwu.service;

import com.ly.xunwu.XunwuApplicationTests;
import com.ly.xunwu.service.search.ISearchService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author liyong
 * @date 2019/9/18
 */
public class SearchServiceTests extends XunwuApplicationTests {

    @Autowired
    private ISearchService searchService;

    @Test
    public void testIndex() {
        Long targetHouseId = 15L;
        searchService.index(targetHouseId);
    }

    @Test
    public void testRemove(){
        searchService.remove(15L);
    }
}
