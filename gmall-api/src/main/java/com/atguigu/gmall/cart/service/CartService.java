package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.SkuResponse;

public interface CartService {

    SkuResponse addToCart(Long skuId, Integer num, String token);

    boolean deleteCart(Long skuId, String cartKey);

    boolean updateCount(Long skuId, Integer num, String cartKey);

    Cart cartItemsList(String cartKey);

    boolean checkCart(Long skuId, Integer flag, String cartKey);

}
