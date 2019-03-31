package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 优惠卷表 服务类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
public interface CouponService extends IService<Coupon> {

    Map<String, Object> pageCouponList(Integer pageNum, Integer pageSize, String name, Integer type);
}
