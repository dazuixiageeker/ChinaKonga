package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description 渲染控制层
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Controller
@RequestMapping("/wcart")
public class CartController {

    @Autowired
    private CartFeign cartFeign;

    @RequestMapping("/list")
    public String list(Model model){
        //集合查询
        Result<List<OrderItem>> result = cartFeign.list();
        model.addAttribute("cartList", result.getData());
        return "cart";
    }

    /**
     * 异步添加购物车数据
     * @param num
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping("/json/add")
    public Result<List<OrderItem>> add(@PathVariable(value="num") Integer num, @PathVariable(value="id") Long id){

        //添加购物车
        cartFeign.add(num,id);
        //查询所有购物车集合
        Result<List<OrderItem>> cartResult = cartFeign.list();
        return cartResult;

    }
}
