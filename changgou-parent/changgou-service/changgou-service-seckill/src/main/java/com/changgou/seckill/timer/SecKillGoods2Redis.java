package com.changgou.seckill.timer;

import com.changgou.entity.DateUtil;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author MartinMYZ
 * @description 将秒杀商品压入redis
 * @created at 2019/8/28
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Component
public class SecKillGoods2Redis {

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //排班表
    @Scheduled(cron = "0/30 * *  * * ?")
    public void pushGoods2Redis() {

        //获取时间的区间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            //拼接时间段
            String extName = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
             //封装查询条件
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", 1); //审核过的商品
            criteria.andGreaterThan("stockCount", 0); //库存大于0
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu); //商品开始时间
            criteria.andLessThanOrEqualTo("endTime", DateUtil.addDateHour(dateMenu, 2)); //商品结束时间

            //排除重复的key
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            //从数据库查询
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //压入redis缓存
            if (seckillGoods != null && seckillGoods.size() > 0) {
                for (SeckillGoods seckillGood : seckillGoods) {
                    redisTemplate.boundHashOps("SeckillGoods_" + extName).put(seckillGood.getId().toString(), seckillGood);

                    //商品数据队列存储,防止高并发超卖
                    Long[] ids = pushIds(seckillGood.getStockCount(), seckillGood.getId());
                    redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGood.getId()).leftPushAll(ids);

                    //自增计数器
                    redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGood.getId(),seckillGood.getStockCount());
                }
            }
        }
    }

    /***
     * 将商品ID存入到数组中
     * @param len:长度
     * @param id :值
     * @return
     */
    public Long[] pushIds(int len,Long id){
        Long[] ids = new Long[len];
        for (int i = 0; i <ids.length ; i++) {
            ids[i]=id;
        }
        return ids;
    }
}
