package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.admin.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.TestSearch;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.to.PmsProductParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@CrossOrigin
@Api(description = "商品管理模块")
@RestController
@RequestMapping("/product")
public class PmsProductController {

    @Reference
    private ProductService productService;

    @Reference
    private TestSearch testSearch;

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

    @ApiOperation("添加商品")
    @PostMapping("/create")
    public Object addProduct(@Valid @RequestBody PmsProductParam productParam,
                             BindingResult bindingResult){
        productService.createProduct(productParam);
        return null;
    }

    //product/update/publishStatus?ids=59&publishStatus=1
    @ApiOperation("批量上下架")
    @PostMapping(value = "/update/publishStatus")
    public Object updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("publishStatus") Integer publishStatus) {
        //TODO 批量上下架

        productService.publishStatus(ids,publishStatus);

        return new CommonResult().success(null);
    }


    @ApiOperation("测试search")
    @PostMapping(value = "/test")
    public Object updatePublishStatus() {
        //TODO 批量上下架
        testSearch.testSearch();
        return new CommonResult().success(null);
    }
}
