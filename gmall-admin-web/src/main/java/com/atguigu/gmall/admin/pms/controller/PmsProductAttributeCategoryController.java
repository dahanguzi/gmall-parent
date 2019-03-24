package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(description = "商品属性分类管理")
@CrossOrigin
@RestController
@RequestMapping("/productAttribute/category")
public class PmsProductAttributeCategoryController {

    @Reference
    ProductAttributeCategoryService productAttributeCategoryService;

    @ApiOperation("获取商品属性分类")
    @GetMapping("/list")
    public Object getList(@RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNum) {

        // 分页获取所有商品属性分类
        //这是一个分页
        Map<String, Object> map = productAttributeCategoryService.pageProductAttributeCategory(pageSize, pageNum);

        return new CommonResult().success(map);
    }
}
