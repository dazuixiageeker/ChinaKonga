package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/25
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Controller
@RequestMapping("/worder")
public class OrderController {

    @Autowired
    private OrderFeign orderFeign;


    @Autowired
    private AddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;
    /**
     * 订单结算信息查询
     * @param model
     * @return
     */
    @RequestMapping("/ready/order")
    public String readyOrder(Model model){
        Result<List<OrderItem>> cartList = cartFeign.list();
        Result<List<Address>> addrlist = addressFeign.list();
        model.addAttribute("address", addrlist.getData());
        model.addAttribute("carts", cartList.getData());
        return "order";
    }
    
    
    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Order order){
        Result result = orderFeign.add(order);
        return result;
    }


}
