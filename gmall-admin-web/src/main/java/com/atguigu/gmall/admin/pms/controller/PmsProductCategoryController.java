package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.to.PmsProductCategoryWithChildrenItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@EnableDubbo
@Api(description = "后台商品分类管理模块")
@CrossOrigin
@RestController
@RequestMapping("/productCategory")
public class PmsProductCategoryController {

    @Reference
    private ProductCategoryService productCategoryService;

    @ApiOperation("获取商品分类列表")
    @GetMapping("/list/withChildren")
    public Object getProductCategoryList(){

        List<PmsProductCategoryWithChildrenItem> productCategoryList = productCategoryService.listWithChildren();

        return new CommonResult().success(productCategoryList);
    }
}
