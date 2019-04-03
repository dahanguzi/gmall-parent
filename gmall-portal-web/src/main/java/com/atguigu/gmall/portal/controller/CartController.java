package com.atguigu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@Api(description = "购物车模块")
@CrossOrigin//跨域注解
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

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

        SkuResponse skuResponse = cartService.addToCart(skuId, num, cartKey);

        return new CommonResult().success(skuResponse);
    }

    @ApiOperation("删除商品")
    @PostMapping("/delete")
    public CommonResult removeToCart(
            @ApiParam(value = "需要移除的商品的skuId")
            @RequestParam("skuId") Long skuId,

            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,

            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey){

        RpcContext.getContext().setAttachment("gmallusertoken", token);

        boolean delete = cartService.deleteCart(skuId,cartKey);

        return new CommonResult().success(delete);
    }


    @ApiOperation("修改商品数量")
    @PostMapping("/update")
    public CommonResult updateCart(
            @ApiParam(value = "需要修改的商品的skuId")
            @RequestParam("skuId") Long skuId,

            @ApiParam(value = "需要修改的数量")
            @RequestParam("token") Integer num,

            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,

            @ApiParam(value = "传递之前,后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey){

        RpcContext.getContext().setAttachment("gmallusertoken",token);

        boolean upadte = cartService.updateCount(skuId,num,cartKey);
        return new CommonResult().success(upadte);
    }

    @ApiOperation("查询购物车中的商品")
    @GetMapping("/list")
    public CommonResult selectCart(
            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,

            @ApiParam(value = "传递之前,后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey")String cartKey){
        //1、先获取购物车（条件即参数不尽相同）
        //2、再操作购物车中的内容（购物项）
        //3、重新进行存储
        RpcContext.getContext().setAttachment("gmallusertoken",token);

        Cart cart = cartService.cartItemsList(cartKey);
        return new CommonResult().success(cart);
    }

    @PostMapping("/check")
    public CommonResult checkToCart(
            @ApiParam(value = "需要添加的商品的skuId")
            @RequestParam("skuId") Long skuId,
            @ApiParam(value = "需要选中的商品，0不选中，1选中")
            @RequestParam("flag") Integer flag,
            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,
            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey){


        //注意：rpc上下文要隐式传参，参数的名字一定不要用如下关键字
        //token,timeout,async
        RpcContext.getContext().setAttachment("gmallusertoken",token);
        boolean check = cartService.checkCart(skuId,flag,cartKey);

        return new CommonResult().success(check);
    }
}
