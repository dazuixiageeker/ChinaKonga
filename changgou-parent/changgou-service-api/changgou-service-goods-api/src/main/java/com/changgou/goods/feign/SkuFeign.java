package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="goods")
@RequestMapping(value="/sku")
public interface SkuFeign {

    /**
     * 库存减少, (供订单微服务调用)
     * @param username
     * @return
     */
    @PostMapping("/decr/count")
    Result decrCount(@RequestParam(value="username") String username);


    /**
     * 根据审核状态查询sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable String status);

    /***
     * 多条件搜索品牌数据
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false)  Sku sku);

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(value="id",required = true) Long id);

}
