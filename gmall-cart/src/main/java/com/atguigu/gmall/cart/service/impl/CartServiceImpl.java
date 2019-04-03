package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.ums.entity.Member;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Component
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Reference
    private SkuStockService skuStockService;

    @Reference
    private ProductService productService;

    @Override
    public SkuResponse addToCart(Long skuId, Integer num, String cartKey) {

        SkuResponse skuResponse = new SkuResponse();
        //隐式传参的方法
        String token = RpcContext.getContext().getAttachment("gmallusertoken");//远程传输过来的
        //通过token从redis中查询用户的信息
        String memberJson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberJson, Member.class);//解析json字符串

        Long memberId = 0L;
        //获取会员id
        if(member!=null){
            memberId = member.getId();
        }
        //获取会员的昵称
        String memberName = member==null?"":member.getNickname();

        //1、查询这个sku的信息，将其添加到购物车中
        SkuStock skuStock = skuStockService.getSkuInfoById(skuId);
        //2、查询出这个对应的spu信息
        Product product = productService.getProductByIdFromCache(skuStock.getProductId());

        //3、查询优惠券系统
        //List<Coupon> item = couponService.getCouponBtyProductID(product);

        //4、封装成一个cartItem；
        CartItem cartItem = new CartItem(product.getId(),
                skuStock.getId(),
                memberId,
                num,
                skuStock.getPrice(),
                skuStock.getPrice(),
                num,
                skuStock.getSp1(), skuStock.getSp2(), skuStock.getSp3(),
                product.getPic(),
                product.getName(),
                memberName,
                product.getProductCategoryId(),
                product.getBrandName(),
                false,
                "满199减90"
        );

        //memberJson为空，表示这个令牌没数据，没登录；离线购物车流程
        if(StringUtils.isEmpty(memberJson)){
            if(!StringUtils.isEmpty(cartKey)){      //用户有老购物车
                skuResponse.setCartKey(cartKey);

                cartKey = RedisCacheConstant.CART_TEMP +cartKey;//CART_TEMP表示临时token(key)

                addItemToCart(cartItem,num,cartKey);
            }else{      //用户没有购物车，故需要新建一个购物车

                //新建一个购物车
                String replace = UUID.randomUUID().toString().replace("-", "");
                String newCartKey = RedisCacheConstant.CART_TEMP+replace;
                skuResponse.setCartKey(replace);
                addItemToCart(cartItem,num,newCartKey);
            }
        }else{
            //表示用户已经登录，处于在线添加添加购物车流程
            String loginCartKey = RedisCacheConstant.USER_CART+member.getId();//登录的key

            //合并购物车；
            mergeCart(RedisCacheConstant.CART_TEMP+cartKey,loginCartKey);

            //5、放入购物车
            addItemToCart(cartItem,num,loginCartKey);
        }

        skuResponse.setItem(cartItem);
        return skuResponse;
    }


    //添加一条项目到购物车
    private void addItemToCart(CartItem cartItem, Integer num, String cartKey) {

        //分布式redis，拿取到购物车
        RMap<String, String> map = redissonClient.getMap(cartKey);
        boolean b = map.containsKey(cartItem.getProductSkuId()+"");

        if(b){
            //3、购物车已有此项
            String json = map.get(cartItem.getProductSkuId()+"");
            CartItem item = JSON.parseObject(json, CartItem.class);
            cartItem.setNum(item.getNum() + num);
            String string = JSON.toJSONString(item);
            map.put(item.getProductSkuId()+"",string);
        }else{
        //购物车没有此项
            String string = JSON.toJSONString(cartItem);
            map.put(cartItem.getProductSkuId()+"",string);
        }
    }

    //合并购物车,就是对购物车数量的操作
    //采用的是hash的存储结构
    private void mergeCart(String oldCartKey, String newCartKey) {
        //获取老购物车
        RMap<String, String> map = redissonClient.getMap(oldCartKey);

        if(map!=null&&map.entrySet()!=null){
            map.entrySet().forEach((entry)->{
                String value = entry.getValue();
                CartItem item = JSON.parseObject(value, CartItem.class);
                //将老购物车的数据转移过去
                addItemToCart(item,item.getNum(),newCartKey);
                map.remove(item.getProductSkuId()+"");
            });
        }
    }
}
