package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


    @Override
    public Map<String, Object> pageListProduct(Integer pageNum, Integer pageSize) {

        ProductMapper baseMapper = getBaseMapper();

        Page<Product> page = new Page(pageNum,pageSize);
        //QueryWrapper queryWrapper = new QueryWrapper();

        IPage iPage = baseMapper.selectPage(page, null);

        Map<String,Object> map = new HashMap();

        map.put("pageSize",pageSize);
        map.put("totalPage",iPage.getPages());
        map.put("total",iPage.getTotal());
        map.put("pageNum",iPage.getCurrent());
        map.put("list",iPage.getRecords());

        return map;
    }

    @Override
    public Product getProductBaseInfoById(Long id) {

        return null;
    }
}
