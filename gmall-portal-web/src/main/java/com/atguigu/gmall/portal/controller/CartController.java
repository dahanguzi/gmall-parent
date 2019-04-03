package com.atguigu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "购物车模块")
@CrossOrigin//跨域注解
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService CartService;

    @ApiOperation(value = "添加商品到购物车")
    public CommonResult addToCart(
            @ApiParam(value = "需要添加的商品的skuId")
            @RequestParam("skuId") Long skuId,

            @ApiParam(value = "需要添加的商品的数量")
            @RequestParam("num") Integer num,

            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,

            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey
    ){

        //注意：rpc上下文要隐式传参，参数的名字一定不要用如下关键字
        //token,timeout,async
        RpcContext.getContext().setAttachment("gmallusertoken",token);

        SkuResponse skuResponse = CartService.addToCart(skuId, num, cartKey);

        return new CommonResult().success(skuResponse);
    }
}
