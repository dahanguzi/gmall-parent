package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {


    @Override
    public Map<String, Object> pageProductAttributeCategory(Integer pageSize, Integer pageNum) {
        Page<ProductAttributeCategory> pageParam = new Page(pageNum,pageSize);

        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        IPage<ProductAttributeCategory> page = baseMapper.selectPage(pageParam, null);
        Map<String, Object> pageMap = PageUtils.getPageMap(page);
        return pageMap;
    }
}
