package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.mapper.SkuStockMapper;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Service
@Component
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

    @Autowired
    private SkuStockMapper skuStockMapper;

    @Override
    public SkuStock getSkuInfoById(Long skuId) {

        //根据skuId查询，获取skuStock
        SkuStock skuStock = skuStockMapper.selectById(skuId);
        return skuStock;
    }

    @Override
    public List<SkuStock> getAllSkuInfoByProdyctId(Long productId) {

        //根据商品Id查询并获取所有的skuStocks的集合信息
        List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id",productId));
        return skuStocks;
    }
}
