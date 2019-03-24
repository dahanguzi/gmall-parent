package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.to.PmsProductCategoryWithChildrenItem;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Slf4j
@Service
@Component
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<PmsProductCategoryWithChildrenItem> listWithChildren() {

        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();

        String cache = valueOperations.get(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY);

        if(!StringUtils.isEmpty(cache)){
            log.info("redis缓存中查询到相关数据内容");

            List<PmsProductCategoryWithChildrenItem> items = JSON.parseArray(cache, PmsProductCategoryWithChildrenItem.class);
            return items;
        }

        ProductCategoryMapper baseMapper = getBaseMapper();

        List<PmsProductCategoryWithChildrenItem> items = baseMapper.listWithChildren(0);

        //存数据都给一个过期时间比较好；
        String jsonString = JSON.toJSONString(items);
        valueOperations.set(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY,jsonString,3, TimeUnit.DAYS);
        //查某个菜单的所有子菜单
        return items;
    }
}
