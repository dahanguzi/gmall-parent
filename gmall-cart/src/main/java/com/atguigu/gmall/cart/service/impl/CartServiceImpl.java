package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gmall.cart.bean.Cart;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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

    @Override
    public boolean deleteCart(Long skuId, String cartKey) {
        //远程传输，隐式传参
        String token = RpcContext.getContext().getAttachment("gmallusertoken");

        //通过token获取用户的信息
        String memberjson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberjson, Member.class);

        RMap<String, String> map = null;
        if(member == null){     //用户未登录
            //获取未登录时的购物车
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP+cartKey);
        }else{
            map = redissonClient.getMap(RedisCacheConstant.USER_CART+cartKey);
        }

        map.remove(skuId+"");
        return true;
    }

    @Override
    public boolean updateCount(Long skuId, Integer num, String cartKey) {
        //可以通过token来获取用户对象，判断用户是否已经登录
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        //根据key获取value
        String memberjson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberjson, Member.class);

        //分布式集合
        RMap<String, String> map = null;
        //判断用户是否登录
        if(member == null){//表示用户未登录
            //没登录，更新的是游客购物车
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP+cartKey);
        }else{//用户已经登录
            //登录后，更新的是用户购物车
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        //购物车存储数据为hash结构，value值已map的形式进行存储
        //这里的key是结果中map的key，value为购物项的值，以json的形式进行存储
        String s = map.get(skuId + "");
        //解析json得到购物项
        CartItem cartItem = JSON.parseObject(s, CartItem.class);
        //修改购物车对应购物项的数量
        cartItem.setNum(num);
        //修改过后重新进行存储
        String json = JSON.toJSONString(cartItem);
        map.put(skuId+"",json);

        return true;
    }

    @Override
    public Cart cartItemsList(String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        String memberjson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberjson, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else{
            //用户登陆
            //尝试合并购物车
            mergeCart(RedisCacheConstant.CART_TEMP+cartKey,RedisCacheConstant.USER_CART+member.getId());

            //合并完成后再操作
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
            //
        }

        if(map !=null){
            Cart cart = new Cart();
            cart.setItems(new ArrayList<CartItem>());
            map.entrySet().forEach((o)->{
                String json = o.getValue();
                CartItem item = JSON.parseObject(json, CartItem.class);
                cart.getItems().add(item);
            });

            return  cart;
        }else {
            return new Cart();
        }
    }

    @Override
    public boolean checkCart(Long skuId, Integer flag, String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");//远程传输过来的
        String memberJson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberJson, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else {
            //用户登陆
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
        }

        String s = map.get(skuId + "");
        CartItem item = JSON.parseObject(s, CartItem.class);
        item.setChecked(flag==0?false:true);
        String json = JSON.toJSONString(item);
        map.put(skuId + "",json);

        //维护checked字段的set
        String checked = map.get("checked");
        Set<String> checkedSkuIds = new HashSet<>();
        //复杂的泛型数据转换
        if(!StringUtils.isEmpty(checked)){
            //有
            Set<String> strings = JSON.parseObject(checked, new TypeReference<Set<String>>() {
            });
            if(flag == 0){
                //不勾中
                strings.remove(skuId+"");
            }else {
                strings.add(skuId+"");
            }

            String s1 = JSON.toJSONString(strings);
            map.put("checked",s1);

        }else {
            //没有
            checkedSkuIds.add(skuId+"");
            String s1 = JSON.toJSONString(checkedSkuIds);
            map.put("checked",s1);
        }

        return true;
    }


    //添加一条项目到购物车
    private void addItemToCart(CartItem cartItem, Integer num, String cartKey) {

        //分布式集合，拿取到购物车
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
