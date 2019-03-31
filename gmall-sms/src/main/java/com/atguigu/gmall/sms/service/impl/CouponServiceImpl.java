package com.atguigu.gmall.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.mapper.CouponMapper;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <p>
 * 优惠卷表 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Service
@Component
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Override
    public Map<String, Object> pageCouponList(Integer pageNum, Integer pageSize, String name, Integer type) {

        CouponMapper baseMapper = getBaseMapper();
        QueryWrapper queryWrapper = new QueryWrapper();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(type)){
            queryWrapper.like("type",type);
        }

        Page pageParam = new Page(pageNum,pageSize);
        IPage page = baseMapper.selectPage(pageParam, queryWrapper);
        Map<String, Object> map = PageUtils.getPageMap(page);
        return map;
    }
}
