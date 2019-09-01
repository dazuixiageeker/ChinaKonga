package com.changgou.item.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/19
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(value="id") Long spuId){
        pageService.createHtml(spuId);
        return new Result(true, StatusCode.OK,"指定产品静态页生成成功");
    }
}
