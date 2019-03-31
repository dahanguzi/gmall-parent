package com.atguigu.gmall.item.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.to.ProductAllInfos;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Service(version = "1.0")
@Component
public class ItemServiceImpl implements ItemService {

    @Reference
    private SkuStockService skuStockService;

    @Reference
    private ProductService productService;

    @Override
    public ProductAllInfos getProInfo(Long skuId) {

        ProductAllInfos productAllInfos = new ProductAllInfos();

        //1、获取当前sku的详细信息
        SkuStock skuStock = skuStockService.getSkuInfoById(skuId);
        Long productId = skuStock.getProductId();

        //2、获取当前商品的详细信息
        Product product = productService.getById(productId);

        //3、获取所有sku的详细信息，一般采用集合的方式进行存储
        List<SkuStock> skuStocks = skuStockService.getAllSkuInfoByProdyctId(productId);

        //4、获取商品所有销售属性可选值
        List<EsProductAttributeValue> productSaleAttrs = productService.getProductSaleAttrs(productId);

        //5、商品的其他属性值
        List<EsProductAttributeValue> productBaseAttrs = productService.getProductBaseAttrs(productId);

        //6、当前商品设计到的服务

        //封装查询结果
        productAllInfos.setSkuStock(skuStock);
        productAllInfos.setProduct(product);
        productAllInfos.setSkuStocks(skuStocks);
        productAllInfos.setSaleAttr(productSaleAttrs);
        productAllInfos.setBaseAttr(productBaseAttrs);
        return productAllInfos;
    }
}
