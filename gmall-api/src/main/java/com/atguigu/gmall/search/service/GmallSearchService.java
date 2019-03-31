package com.atguigu.gmall.search.service;

import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;

import java.io.IOException;


public interface GmallSearchService {

    boolean saveProductInfoToES(EsProduct esProduct);

    SearchResponse searchProduct(SearchParam param) throws IOException;
}
