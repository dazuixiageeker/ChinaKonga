package com.changgou.content.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/18 13:58
 *****/
@FeignClient(name="goods")
@RequestMapping("/cache")
public interface CacheFeign {

    /**
     * 全部载入缓存
     * @return
     */
    @RequestMapping("/refresh")
    Result refreshCache();
}