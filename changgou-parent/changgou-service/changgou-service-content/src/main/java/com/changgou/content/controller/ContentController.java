package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/14
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@RestController
@RequestMapping("/content")
@CrossOrigin
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 根据category ID进行广告查询
     * @param id
     * @return
     */
    @GetMapping("/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable(value = "id")Long id){
        List<Content> contents = contentService.findByCategory(id);
        return new Result<List<Content>>(true,StatusCode.OK,"广告内容查询成功",contents);
    }

}
