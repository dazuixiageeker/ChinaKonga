package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:Martin MYZ
 * @Description:SeckillOrder业务层接口
 *
 *****/
public interface SeckillOrderService {
    /**
     * 关闭订单,回滚库存
     * @param username
     */
    void closeOrder(String username);


    /**
     * 更新订单状态
     * @param out_trade_no
     * @param transaction_id
     * @param username
     */
    void updatePayStatus(String out_trade_no, String transaction_id, String username);



    /**
     * 查询抢购的订单详情
     * @param username
     * @return
     */
    SeckillStatus queryStatus(String username);

    /**
     * 下单
     */
    Boolean placeOrder(String time, Long id, String username);


    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
     SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();
}
