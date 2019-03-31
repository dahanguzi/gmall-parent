package com.atguigu.gmall.admin.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "营销优惠券模块")
@CrossOrigin
@RestController
@RequestMapping("/coupon")
public class SmsCouponController {

    @Reference
    private CouponService couponService;

    @ApiOperation("获取优惠券列表")
    @GetMapping("/list")
    public Object getPageCouponList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                    @RequestParam(value = "name",required = false) String name,
                                    @RequestParam(value = "type",required = false) Integer type){

        Map<String, Object> map = couponService.pageCouponList(pageNum, pageSize, name, type);

        return new CommonResult().success(map);
    }
}
