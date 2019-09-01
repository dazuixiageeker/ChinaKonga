package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.order.pojo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name="order")
@RequestMapping("/cart")
public interface CartFeign {

    /**
     * 添加购物车
     * @param num
     * @param id
     * @param
     * @return
     */
    @GetMapping("/add")
    public Result add(@PathVariable(value = "num") Integer num,@PathVariable(value = "id") Long id);

    /**
     * 添加购物车
     * @param
     * @return
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> list();

}
