package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.search.service.GmallSearchService;
import com.atguigu.gmall.to.PmsProductParam;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author LZj
 * @since 2019-03-19
 */
@Slf4j
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    SkuStockMapper skuStockMapper;

    @Autowired
    ProductLadderMapper productLadderMapper;

    @Autowired
    ProductFullReductionMapper productFullReductionMapper;

    @Autowired
    MemberPriceMapper memberPriceMapper;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    @Autowired
    ProductMapper productMapper;

    @Reference(version = "1.0")
    GmallSearchService gmallSearchService;

    @Autowired
    JedisPool jedisPool;

    //本线程的对象
    ThreadLocal<Product> productThreadLocal = new ThreadLocal<Product>();

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

    @Override
    public void createProduct(PmsProductParam productParam) {

        //1、保存商品的基本信息

        //2、
        //3、
        //4、
        //5、

        //1、保存商品的基本信息 pms_product（将刚才保存的这个商品的自增id获取出来）【REQUIRED】
        ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();

        //保存SPU和SKU【REQUIRES_NEW】
        psProxy.saveBaseProductInfo(productParam);

        //Require
        psProxy.saveProductLadder(productParam.getProductLadderList());//【REQUIRED_NEW】
        psProxy.saveProductFullReduction(productParam.getProductFullReductionList());
        psProxy.saveMemberPrice(productParam.getMemberPriceList());
        psProxy.saveProductAttributeValue(productParam.getProductAttributeValueList());
        psProxy.updateProductCategoryCount();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Long saveProduct(PmsProductParam productParam){

        ProductMapper baseMapper = getBaseMapper();
        Product product = new Product();
        BeanUtils.copyProperties(productParam,product);

        int insert = baseMapper.insert(product);

        log.debug("插入商品：{}",product.getId());

        //商品信息共享到ThreadLocal
        productThreadLocal.set(product);
        //map.put(Thread.currentThread(),product);
        return  product.getId();
    }

    //2、保存Sku信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSkuInfo(List<SkuStock> skuStocks){
        Product product = productThreadLocal.get();
        //1）、线程安全的。遍历修改不安全
        AtomicReference<Integer> i = new AtomicReference<>(0);
        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        numberFormat.setMaximumIntegerDigits(2);

        skuStocks.forEach(skuStock -> {
            //保存商品id
            skuStock.setProductId(product.getId());
            //SKU编码 k_商品id_自增
            //skuStock.setSkuCode();  两位数字，不够补0
            String format = numberFormat.format(i.get());

            String code = "K_"+product.getId()+"_"+format;
            skuStock.setSkuCode(code);
            //自增
            i.set(i.get() + 1);

            skuStockMapper.insert(skuStock);
        });
    }


    //3、保存商品阶梯价格 到 saveProductLadder【REQUIRES_NEW】
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(List<ProductLadder> list){

        Product product = productThreadLocal.get();
        //Product product1 = map.get(Thread.currentThread());
        //2、保存商品的阶梯价格 到 pms_product_ladder【REQUIRES_NEW】
        for (ProductLadder ladder : list) {
            ladder.setProductId(product.getId());
            productLadderMapper.insert(ladder);
            log.debug("插入ladder{}",ladder.getId());
        }
    }

    //4、保存商品满减价格 到 pms_product_full_reduction【REQUIRES_NEW】
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(List<ProductFullReduction> list){
        Product product = productThreadLocal.get();
        for (ProductFullReduction reduction : list) {
            reduction.setProductId(product.getId());
            productFullReductionMapper.insert(reduction);
        }
    }


    //5、保存商品的会员价格 到 pms_member_price【REQUIRES_NEW】{// int i=10/0}
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberPrice(List<MemberPrice> memberPrices){
        Product product = productThreadLocal.get();
        for (MemberPrice memberPrice : memberPrices) {
            memberPrice.setProductId(product.getId());
            memberPriceMapper.insert(memberPrice);
        }
        //lambda
    }

    //6、保存参数及自定义规格 到 pms_product_attribute_value（）【REQUIRES_NEW】
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public  void saveProductAttributeValue(List<ProductAttributeValue> productAttributeValues){
        Product product = productThreadLocal.get();
        productAttributeValues.forEach((pav)->{
            pav.setProductId(product.getId());
            productAttributeValueMapper.insert(pav);
        });
    }

    //7、更新商品分类数目 【REQUIRES_NEW】
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProductCategoryCount(){
        Product product = productThreadLocal.get();
        Long id = product.getProductCategoryId();

//        ProductCategory productCategory = new ProductCategory();
//        productCategory.setId(id);
//        productCategory.setProductCount()

        productCategoryMapper.updateCountById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseProductInfo(PmsProductParam productParam){
        ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
        //Required
        psProxy.saveProduct(productParam);//【REQUIRES_NEW】
        //Required
        psProxy.saveSkuInfo(productParam.getSkuStockList());
    }

    @Override
    public void publishStatus(List<Long> ids, Integer publishStatus) {
        //1、上架/下架
        if(publishStatus==1){
            publishProduct(ids);
        }else{
            removeProduct(ids);
        }
    }

    private void publishProduct(List<Long> ids){
        //1、查当前需要上架的商品的sku信息和spu信息
        ids.forEach((id)->{
            //1）、SPU
            Product product = productMapper.selectById(id);
            //2）、需要上架的SKU
            List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", product.getId()));
            //3）、这个商品所有的参数值
            List<EsProductAttributeValue> attributeValues = productAttributeValueMapper.selectProductAttrValues(product.getId());
            //4）、改写信息，将其发布到es；统计上架状态是否全部完成
            AtomicReference<Integer> count = new AtomicReference<Integer>(0);

            System.out.println("______"+gmallSearchService);
            skuStocks.forEach((sku)->{
                EsProduct esProduct = new EsProduct();
                BeanUtils.copyProperties(product,esProduct);
                //5）改写商品的标题，加上sku的销售属性
                esProduct.setName(product.getName()+" "+sku.getSp1()+" "+sku.getSp2()+""+sku.getSp3());
                esProduct.setPrice(sku.getPrice());
                esProduct.setStock(sku.getStock());
                esProduct.setSale(sku.getSale());
                esProduct.setAttrValueList(attributeValues);
                //6）、改写id，使用sku的id
                esProduct.setId(sku.getId());//直接改为sku的id
                //7)、保存到es中；//5个成了3个败了。不成

                boolean es = gmallSearchService.saveProductInfoToES(esProduct);
                count.set(count.get()+1);
                if(es){
                    //保存当前的id，list.add(id);

                }
            });

            //8）、判断是否完全上架成功，成功改数据库状态
            if(count.get()==skuStocks.size()){
                //9）、修改数据库状态;都是包装类型允许null值
                Product update = new Product();
                update.setId(product.getId());
                update.setPublishStatus(1);
                productMapper.updateById(update);
            }else{
                //9）、成功的撤销操作；来保证业务数据的一致性；
                //es有失败  list.forEach(remove());
            }
        });
    }

    //下架
    private void removeProduct(List<Long> ids){


    }

    //获取商品的销售属性值
    @Override
    public List<EsProductAttributeValue> getProductSaleAttrs(Long productId) {

        return productMapper.getProductSaleAttrs(productId);
    }

    //获取商品的基本属性值
    @Override
    public List<EsProductAttributeValue> getProductBaseAttrs(Long productId) {

        return productMapper.getProductBaseAttrs(productId);
    }

    @Override
    public Product getProductByIdFromCache(Long productId) {
        Jedis jedis = jedisPool.getResource();
        //从缓存中获取数据
        //考虑缓存穿透的问题
        /*
            缓存穿透问题：缓存与数据库中都查询不到相关数据，频繁的查询数据库
            防止数据库数据更新，缓存中的数据设置有效时间

            （1）雪崩指的是大面积的key失效
            （2）而击穿指的是在某一时刻大量请求查询同一个key，而该key失效

              应用级锁（synchronized/lock）
              分布式锁（）
         */
        //1、从缓存中查询数据
        String s = jedis.get(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId);
        Product product = null;

        //2、判断缓存中是否有数据，有的话就返回，没有就查询数据库
        if(StringUtils.isEmpty(s)){
            //表示当redis中不存在key及对应value值时，加上锁并去数据库中查询
            //Long lock = jedis.setnx("lock", "123");
            //占坑的时候，我们要给一个唯一标识UUID
            String token = UUID.randomUUID().toString();

            String lock = jedis.set("lock", token, SetParams.setParams().ex(5).nx());
            if(lock == null){
                jedis.expire("lock",5);
                try{
                    product = productMapper.selectById(productId);
                    //String stringValue = product.toString();
                    String json = JSON.toJSONString(product);

                    //为了防止缓存雪崩问题的出现，可以为缓存有效时间设定随机值，避免雪崩
                    int anInt = new Random().nextInt(2000);

                    //防止缓存击穿，不管查到与否都将其放在redis中
                    if(product == null){
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId,60+anInt,json);
                    }else{
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId,60*60*24*3+anInt,json);
                    }

                }finally{
                    //删锁的问题，如果由于业务超出的锁的自动删除时间；我们直接按照key删除锁。会导致删掉别人的锁
//                    if(token.equals(jedis.get("lock"))){
//                        //这样有没有问题？判断是相等，但是正好锁过期？数值正在网络传输中锁过期了怎么办？
//                        //比较与删除也应该原子操作
//                        //脚本删除锁  lua
//                        jedis.del("lock");
//                    }

                    /**
                     * get
                     * expire
                     */
                    String script =
                            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("lock"),Collections.singletonList(token));
                }
            }else{
                try {
                    Thread.sleep(1000);
                    getProductByIdFromCache(productId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }else{
                product = JSON.parseObject(s, Product.class);
            }

        jedis.close();
        return product;
    }
}
