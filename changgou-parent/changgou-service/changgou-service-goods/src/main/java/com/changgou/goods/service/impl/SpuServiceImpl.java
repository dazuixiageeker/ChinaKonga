package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.IdWorker;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.CategoryBrand;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired(required = false)
    private SpuMapper spuMapper;

    @Autowired(required = false)
    private SkuMapper skuMapper;

    @Autowired(required = false)
    private IdWorker idWorker;

    @Autowired(required = false)
    private CategoryMapper categoryMapper;
    @Autowired(required = false)
    private BrandMapper brandMapper;

    @Autowired(required = false)
    private CategoryBrandMapper categoryBrandMapper;


    @Override
    public void realDelete(Long id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 还原删除
     * @param id
     */
    @Override
    public void withdrawDelete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        spu.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 逻辑删除
     * @param id
     */
    @Override
    public void logicDelete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if("1".equals(spu.getIsMarketable())){
            throw new RuntimeException("商品已上架,不能删除!");
        }else {
            spu.setIsDelete("1");
            spuMapper.updateByPrimaryKey(spu);
        }
    }

    /**
     * 批量上下架
     * @param ids
     * @param isMarketable
     */
    @Override
    public void isShows(Long[] ids, String isMarketable) {
        Spu spu = new Spu();
        //封装条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        //更新isMarketable
        spu.setIsMarketable(isMarketable);
        spuMapper.updateByExampleSelective(spu,example);
    }


    /**
     * 商品上下架
     * @param id
     * @param isMarketable
     */
    @Override
    public void isShow(Long id, String isMarketable) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(!"1".equals(spu.getStatus()) || spu.getIsDelete().equals("1")){
            //商品已删除, 或审核未通过
            throw new RuntimeException("不能操作");
        }
        spu.setIsMarketable(isMarketable);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品审核
     * @param id
     * @param status
     */
    @Override
    public void audit(Long id, String status) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if("1".equals(spu.getIsDelete())) {
            //商品已删,无须审核
            throw new RuntimeException("商品已删除,无法审核");
        }
        spu.setStatus("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }
    /**
     * 保存商品
     * @param goods
     */
   /* @Override
    public void saveGoods(Goods goods) {
        //保存Spu
        Spu spu = goods.getSpu();
        //未上架
        spu.setIsMarketable("0");
        //未删除
        spu.setIsDelete("0");
        //待审核
        spu.setStatus("0");
        //生成唯一ID号
        long spuId = idWorker.nextId();
        spu.setId(spuId);
        //保存spu
        spuMapper.insertSelective(spu);
        //保存sku
        List<Sku> skuList = goods.getSkuList();
        if(skuList != null && skuList.size()>0){
            for (Sku sku : skuList) {
                long skuId = idWorker.nextId();
                sku.setId(skuId);
                sku.setSpuId(spuId);
                //设置库存的商品名称
                String name = spu.getName()+ " "+ spu.getCaption();
                //规格和参数,拿到json串
                String spec = sku.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);
                if(specMap != null){
                Set<Map.Entry<String,String>> entrySet = specMap.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        name += "" + entry.getValue();
                    }
                }
                sku.setName(name);
                //设置创建时间
                sku.setCreateTime(new Date());
                //设置更新时间
                sku.setUpdateTime(new Date());
                //设置类别ID(3级即可)
                sku.setCategoryId(spu.getCategory3Id());
                //设置sku字段:类别名
                sku.setCategoryName(categoryMapper.selectByPrimaryKey(spu.getCategory3Id()).getName());
                //设置sku字段:品牌名称
                sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
                //设置状态status 1-正常,2-下架,3-删除
                sku.setStatus("1");
                skuMapper.insertSelective(sku);
            }


        }
        //添加品牌和分类的关联
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if(count == 0){
            categoryBrandMapper.insertSelective(categoryBrand);
        }
    }*/

    /**
     * 保存&更改商品
     *
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        //保存Spu
        Spu spu = goods.getSpu();
        if (spu.getId() == null) {
            //商品不存在,执行新增操作
            //设置未上架
            spu.setIsMarketable("0");
            //未删除
            spu.setIsDelete("0");
            //待审核
            spu.setStatus("0");
            //生成唯一ID号
            long spuId = idWorker.nextId();
            spu.setId(spuId);
            //保存spu
            spuMapper.insertSelective(spu);
        }else{
            //更新操作
            //修改审核状态
            spu.setStatus("0");
            spuMapper.updateByPrimaryKeySelective(spu);
            //删除库存数量
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
            //下面再更新
        }

        //保存sku
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null && skuList.size() > 0) {
            for (Sku sku : skuList) {
                long skuId = idWorker.nextId();
                sku.setId(skuId);
                sku.setSpuId(spu.getId());
                //设置库存的商品名称
                String name = spu.getName() + " " + spu.getCaption();
                //规格和参数,拿到json串
                String spec = sku.getSpec();
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                if (specMap != null) {
                    Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        name += "" + entry.getValue();
                    }
                }
                sku.setName(name);
                //设置创建时间
                sku.setCreateTime(new Date());
                //设置更新时间
                sku.setUpdateTime(new Date());
                //设置类别ID(3级即可)
                sku.setCategoryId(spu.getCategory3Id());
                //设置sku字段:类别名
                sku.setCategoryName(categoryMapper.selectByPrimaryKey(spu.getCategory3Id()).getName());
                //设置sku字段:品牌名称
                sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
                //设置状态status 1-正常,2-下架,3-删除
                sku.setStatus("1");
                skuMapper.insertSelective(sku);
            }


        }
        //添加品牌和分类的关联
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            categoryBrandMapper.insertSelective(categoryBrand);
        }
    }

    /**
     * 根据ID查询商品明细
     *
     * @param spuId
     * @return
     */
    @Override
    public Goods findGoodsById(Long spuId) {
        //获得spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //封装查询条件
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spuId);
        //查询获得sku列表
        List<Sku> skuList = skuMapper.selectByExample(example);
        //封装返回
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }


    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
