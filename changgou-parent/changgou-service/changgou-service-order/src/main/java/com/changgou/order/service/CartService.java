package com.changgou.order.service;


import com.changgou.order.pojo.OrderItem;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车
     * @param num 购买数量
     * @param id 购买ID
     * @param username 用户名
     */
    void add(Integer num, Long id, String username);

    /**
     * 根据用户名查询购物车列表
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
