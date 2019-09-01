package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {

    @Update("update tb_sku SET num=num-#{num},sale_num=sale_num+#{num} where id=#{skuId} and num >=#{num}")
    int decrCount(Object orderItem);
}
