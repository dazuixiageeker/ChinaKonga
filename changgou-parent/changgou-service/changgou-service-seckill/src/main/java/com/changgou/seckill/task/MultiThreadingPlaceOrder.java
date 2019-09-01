package com.changgou.seckill.task;

import com.changgou.entity.IdWorker;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author MartinMYZ
 * @description 异步抢单
 * @created at 2019/8/28
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Component
public class MultiThreadingPlaceOrder {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Async
    public void createOrder(){
        //
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("orderQueue").rightPop();

        try {
            //从队列中获取一个商品
            Object sgood = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
            if (sgood == null){
                //清理当前用户的排队信息
                clearQueue(seckillStatus);
                return;
            }

            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();
            String username = seckillStatus.getUsername();

            //redis中查询,
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id.toString());
            //判断商品是否售罄
            if (seckillGoods == null || seckillGoods.getStockCount() <= 0){
                throw new RuntimeException("对不起, 该商品已售罄");
            }
            //封装订单数据
            SeckillOrder seckillOrder = new SeckillOrder(idWorker.nextId(),id,seckillGoods.getCostPrice(),username,new Date(),"0");
            //存入redis
            redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);
            //扣减库存
            //seckillGoods.setStockCount(seckillGoods.getStockCount()-1);  //只能秒杀一件商品;
            Long surplusCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(id.toString(), -1); //商品数量递减
            seckillGoods.setStockCount(surplusCount.intValue()); //更新库存数量
            //if(seckillGoods.getStockCount() < 0 ){
            if(surplusCount <= 0 ){
                // 并且将数据同步到MySQL中（定时器就不会将该商品在写入内存，因为做了判断）
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //库存小于0, 将商品从redis中删除, 不在出现在页面上
                redisTemplate.boundHashOps("SeckillGoods_"+ time).delete(id.toString());
            }else{
                //有库存,更新redis
                redisTemplate.boundHashOps("SeckillGoods_"+ time).put(id.toString(),seckillGoods);
            }

            //抢单成功,更新用户的抢单状态
            seckillStatus.setStatus(2); //排队-----秒杀待支付
            seckillStatus.setOrderId(seckillOrder.getId()); //订单ID
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney())); //订单金额
            redisTemplate.boundHashOps("UserQueueStatus").put(id,seckillStatus);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    //清理用户的排队信息
    private void clearQueue(SeckillStatus seckillStatus) {
        //清理排队标示
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
        //清理抢单标示
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
    }

}
