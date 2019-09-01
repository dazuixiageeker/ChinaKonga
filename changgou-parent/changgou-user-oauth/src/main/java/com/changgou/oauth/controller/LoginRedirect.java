package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author MartinMYZ
 * @description 登录跳转
 * @created at 2019/8/25
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {


    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping("/login")
    public String login(@RequestParam(value = "ReturnUrl",required = false)String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "  ";
    }
}
