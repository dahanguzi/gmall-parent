package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.ums.entity.Admin;
import com.atguigu.gmall.ums.mapper.AdminMapper;
import com.atguigu.gmall.ums.service.AdminService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Service
@Component
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin loginByUsername(String username) {

        AdminMapper baseMapper = getBaseMapper();
        Admin admin = baseMapper.selectOne(new QueryWrapper<Admin>().eq("username", username));
        return admin;
    }

    @Override
    public Admin getAdminByUsername(String userName) {

        AdminMapper baseMapper = getBaseMapper();
        Admin admin = baseMapper.selectOne(new QueryWrapper<Admin>().eq("username", userName));
        return admin;
    }
}
