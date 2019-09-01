package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据分类ID查询品牌列表
     * @param categoryId
     * @return
     */
    @Select("select b.id, b.name from tb_brand b, tb_category_brand cb where" +
            " b.id=cb.brand_id and cb.category_id = #{value}")
    List<Brand> findBrandsByCategoryId(Integer categoryId);
}
