package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
public interface ProductService extends IService<Product> {


    Map<String,Object> pageListProduct(Integer pageNum, Integer pageSize);

    Product getProductBaseInfoById(Long id);
}
