package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@Api(tags = "PmsBrandController",description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {

    @Reference
    private BrandService brandService;

    @ApiOperation("根据品牌名称分页获取品牌列表")
    @GetMapping("/list")
    public Object getBrandList(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        // 根据品牌名称分页获取品牌列表
        Map<String,Object> brandPageInfo =  brandService.pageBrand(keyword,pageNum,pageSize);

        return new CommonResult().success(brandPageInfo);
    }
}
