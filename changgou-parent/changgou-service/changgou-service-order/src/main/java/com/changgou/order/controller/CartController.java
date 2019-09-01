package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.entity.TokenParser;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description 购物车
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenParser tokenParser;

    /**
     * 添加购物车
     * @param num
     * @param id
     * @param
     * @return
     */
    @GetMapping("/add/{id}/{num}")
    public Result add(@PathVariable(value="id") Long id,@PathVariable(value="num") Integer num){
        //用户名
        String username =tokenParser.getUserInfo().get("username");
        //添加购物车
        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"购物车添加成功");
    }

    /**
     * 添加购物车
     * @param
     * @return
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> list(){
        String username = tokenParser.getUserInfo().get("username");
        //添加购物车
        List<OrderItem> list = cartService.list(username);
        return new Result(true, StatusCode.OK,"购物车列表查询成功",list);
    }


}
