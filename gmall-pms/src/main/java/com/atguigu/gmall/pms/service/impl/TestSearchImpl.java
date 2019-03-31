package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.service.TestSearch;
import com.atguigu.gmall.search.service.GmallSearchService;
import org.springframework.stereotype.Component;

@Service
@Component
public class TestSearchImpl implements TestSearch {

    @Reference//(version = "1.0")
    GmallSearchService searchService;

    @Override
    public void testSearch() {
        System.out.println(searchService);
    }
}
