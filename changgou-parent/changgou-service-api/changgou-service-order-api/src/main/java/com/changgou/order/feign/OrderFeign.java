package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="order")
@RequestMapping("/order")
public interface OrderFeign {

    /**
     * 添加购物车
     * @param
     * @return
     */
    @PostMapping
    Result add(@RequestBody Order order);


}
