package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/15
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */

@RestController
@CrossOrigin
@RequestMapping(value="/search")
public class SkuController {
    @Autowired
    private SkuService skuService;

    /**
     * 导入数据
     * @return
     */
    @GetMapping("/import")
    public Result search(){
        skuService.importSku();
        return new Result(true, StatusCode.OK, "导入数据到索引库成功");
    }


    /**
     * 关键字检索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap){

        Map<String, Object> resultMap = skuService.search(searchMap);
        return resultMap;
    }
}
