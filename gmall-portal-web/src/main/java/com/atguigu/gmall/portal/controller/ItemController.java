package com.atguigu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.to.ProductAllInfos;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

@Api(description = "商品详情")
@CrossOrigin
@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference(version = "1.0")
    private ItemService itemService;

    @GetMapping(value = "/{skuId}.html",produces = "application/json")
    public ProductAllInfos getProductInfo(@PathVariable(name = "skuId")Long skuId){

        //获取商品的所有信息
        ProductAllInfos productAllInfos = itemService.getProInfo(skuId);
        return productAllInfos;
    }
}
