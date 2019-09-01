package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="search")
@RequestMapping("/search")
public interface SkuFeign {

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
     Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap);
}
