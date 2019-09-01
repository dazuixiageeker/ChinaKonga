package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description 购物车实现类
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void add(Integer num, Long id, String username) {

        if(num <=0 ){
            //如果传入的数量<=0,则删除该商品的购物车数据
            redisTemplate.boundHashOps("Cart_"+ username).delete(id);
            return;
        }

        //1. 调用feign根据ID获得sku,及spu,
        Result<Sku> skuResult = skuFeign.findById(id);
        if (skuResult != null && skuResult.isFlag()) {
            //获得sku
            Sku sku = skuResult.getData();
            //获得spu
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            //将sku,spu封装到orderitem中
            OrderItem orderItem = sku2OrderItem(sku, spuResult.getData(), num);
            /**
             * 购物车存入redis
             * namespace=Cart_[username]
             * key = id(sku)
             * value=orderItem
             */
            redisTemplate.boundHashOps("Cart_" + username).put(id, orderItem);
        }


    }
    /**
     * 将sku,spu转换为orderitem
     * @param sku
     * @param spu
     * @param num
     * @return
     */
    private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*orderItem.getPrice());       //单价*数量
        orderItem.setPayMoney(num*orderItem.getPrice());    //实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight()*num);           //重量=单个重量*数量

        //分类ID设置
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }


    /**
     * 查询购物车列表
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> list = redisTemplate.boundHashOps("Cart_" + username).values();
        return list;
    }

}
