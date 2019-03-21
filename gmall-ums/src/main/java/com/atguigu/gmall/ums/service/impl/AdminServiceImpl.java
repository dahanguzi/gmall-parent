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
@Component
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin loginByUsername(String uesrname) {

        AdminMapper baseMapper = getBaseMapper();
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",uesrname);
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public Admin getAdminByUsername(String userName) {
        AdminMapper adminMapper = getBaseMapper();
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", userName));

        return admin;
    }
}
