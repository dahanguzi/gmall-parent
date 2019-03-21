package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.atguigu.gmall.admin.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@EnableDubbo
@CrossOrigin
@Api(description = "商品管理模块")
@RestController
@RequestMapping("/product")
public class PmsProductController {

    @Reference
    private ProductService productService;

    @ApiOperation("根据条件进行分页查询")
    @GetMapping("/list")
    public Object getPagelist(PmsProductQueryParam productQueryParam,
                              @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){

        Map<String,Object> listProduct = productService.pageListProduct(pageNum, pageSize);

        return new CommonResult().success(listProduct);
    }


    @ApiOperation("根据商品id获取商品编辑信息")
    @GetMapping(value = "/updateInfo/{id}")
    public Object getUpdateInfo(@PathVariable Long id) {
        //TODO 根据商品id获取商品编辑信息
        Product result = productService.getProductBaseInfoById(id);
        return new CommonResult().success(null);
    }

    /*@ApiOperation("添加商品")
    @PostMapping("/addProduct")
    public Object addProduct(@ApiParam(name = "",value = "",required = true)
                             @RequestParam ){

    }*/
}
