package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.ums.entity.AdminLoginLog;
import com.atguigu.gmall.ums.mapper.AdminLoginLogMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户登录日志表 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Service
public class AdminLoginLogServiceImpl extends ServiceImpl<AdminLoginLogMapper, AdminLoginLog> implements IService<AdminLoginLog> {

}
