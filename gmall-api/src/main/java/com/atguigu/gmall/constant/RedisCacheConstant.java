package com.atguigu.gmall.constant;

public class RedisCacheConstant {

    public static final String PRODUCT_CATEGORY_CACHE_KEY = "gmall:product:category:cache";

    //redis中的key
    public static final String PRODUCT_INFO_CACHE_KEY = "gmall:product:info:";

    public static final String USER_INFO_CACHE_KEY = "gmall:user:info:";

    public static final long USER_INFO_TIMEOUT = 3L;//默认过期三天
    public static final String CART_TEMP = "gmall:cart:temp:";
    public static final String USER_CART = "gmall:cart:user:";
}
