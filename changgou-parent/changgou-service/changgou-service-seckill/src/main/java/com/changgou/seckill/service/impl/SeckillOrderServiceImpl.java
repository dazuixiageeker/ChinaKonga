package com.changgou.seckill.service.impl;

import com.changgou.entity.IdWorker;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingPlaceOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:Martin MYZ
 * @Description:SeckillOrder业务层接口实现类
 *
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired(required = false)
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private MultiThreadingPlaceOrder multiThreadingPlaceOrder;

    @Override
    public void closeOrder(String username) {
        //将消息转换成SeckillStatus
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundHashOps("UserQueueStatus").get(username);
        //获取Redis中订单信息
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        //如果Redis中有订单信息，说明用户未支付
        if (seckillStatus != null && seckillOrder != null){
            //删除订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            //回滚库存
            //1)从Redis中获取该商品
           SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
            //2)如果Redis中没有，则从数据库中加载
            if (seckillGoods == null){
                 seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
            }
            //3)数量+1  (递增数量+1，队列数量+1)
            Long surplusCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
            seckillGoods.setStockCount(surplusCount.intValue());
            redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).leftPush(seckillStatus.getGoodsId());
            //4)数据同步到Redis中
            redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId().toString(),seckillGoods);
             //清理排队标示
            redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
             //清理抢单标示
            redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
        }
    }

    @Override
    public void updatePayStatus(String out_trade_no, String transaction_id, String username) {
        //订单数据从Redis数据库查询出来
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        //修改状态
        seckillOrder.setStatus("1");
        //支付时间
        seckillOrder.setPayTime(new Date());
        //同步到MySQL中
        seckillOrderMapper.insertSelective(seckillOrder);
        //清空Redis缓存
        redisTemplate.boundHashOps("SeckillOrder").delete(username);
        //清空用户排队数据
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        //删除抢购状态信息
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }

    /**
     * 查询抢购详情
     * @param username
     * @return
     */
    @Override
    public SeckillStatus queryStatus(String username) {
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundHashOps("UserQueueStatus").get(username);

        return seckillStatus;
    }

    /**
     * 下单
     * @param time 秒杀时间段
     * @param id  秒杀商品ID
     * @param username 客户ID
     * @return
     */
    @Override
    public Boolean placeOrder(String time, Long id, String username) {
        //递增,判断是否排队
        Long queueCount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        if (queueCount > 1){
            //100,表示有重复抢单
            throw new RuntimeException(String.valueOf(StatusCode.REPERROR));
        }


        //将用户下单信息封装到队列中
        SeckillStatus seckillStatus = new SeckillStatus(username,new Date(),1,id,time);
        redisTemplate.boundListOps("orderQueue").leftPush(seckillStatus);
        //将抢单状态封装到redis中
        redisTemplate.boundHashOps("UserQueueStatus").put(id,seckillStatus);
        //多线程下单
        multiThreadingPlaceOrder.createOrder();
        return true;
    }

    /**
     * SeckillOrder条件+分页查询
     * @param seckillOrder 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder){
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder){
        Example example=new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(seckillOrder!=null){
            // 主键
            if(!StringUtils.isEmpty(seckillOrder.getId())){
                    criteria.andEqualTo("id",seckillOrder.getId());
            }
            // 秒杀商品ID
            if(!StringUtils.isEmpty(seckillOrder.getSeckillId())){
                    criteria.andEqualTo("seckillId",seckillOrder.getSeckillId());
            }
            // 支付金额
            if(!StringUtils.isEmpty(seckillOrder.getMoney())){
                    criteria.andEqualTo("money",seckillOrder.getMoney());
            }
            // 用户
            if(!StringUtils.isEmpty(seckillOrder.getUserId())){
                    criteria.andEqualTo("userId",seckillOrder.getUserId());
            }
            // 创建时间
            if(!StringUtils.isEmpty(seckillOrder.getCreateTime())){
                    criteria.andEqualTo("createTime",seckillOrder.getCreateTime());
            }
            // 支付时间
            if(!StringUtils.isEmpty(seckillOrder.getPayTime())){
                    criteria.andEqualTo("payTime",seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if(!StringUtils.isEmpty(seckillOrder.getStatus())){
                    criteria.andEqualTo("status",seckillOrder.getStatus());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(seckillOrder.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if(!StringUtils.isEmpty(seckillOrder.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",seckillOrder.getReceiverMobile());
            }
            // 收货人
            if(!StringUtils.isEmpty(seckillOrder.getReceiver())){
                    criteria.andEqualTo("receiver",seckillOrder.getReceiver());
            }
            // 交易流水
            if(!StringUtils.isEmpty(seckillOrder.getTransactionId())){
                    criteria.andEqualTo("transactionId",seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder){
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder){
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id){
        return  seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
