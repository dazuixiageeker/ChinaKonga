package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.canal.mq.queue.TopicQueue;
import com.changgou.canal.mq.send.TopicMessageSender;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Message;
import com.changgou.entity.Result;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description 对表增删改查操作进行监听
 * @created at 2019/8/14
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@CanalEventListener
public class CanalDataEventListener {


    @Autowired(required = false)
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private TopicMessageSender topicMessageSender;

    /***
     * 增加规格,分类数据监听
     * 同步数据到Redis
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_goods", table = {"tb_spu" }, eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {


        //操作类型
        int number = eventType.getNumber();
        //操作的数据ID
        String id = getColumn(rowData, "id");
        //封装message
        Message message = new Message(number,id, TopicQueue.TOPIC_QUEUE_SPU,TopicQueue.TOPIC_EXCHANGE_SPU);
        //发送消息
        topicMessageSender.sendMessage(message);
    }





    /***
     * 增加数据监听
     * @param eventType 监听对数据库操作的事件, 比如新增,删除,更新
     * @param rowData 监听到的数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    /***
     * 修改数据监听
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData) {
        System.out.println("UpdateListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    /***
     * 删除数据监听
     * @param eventType
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType) {
        System.out.println("DeleteListenPoint");
    }

   /* *//***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     *//*
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content_category", "tb_content"}, eventType = CanalEntry.EventType.UPDATE)
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("DeleteListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }*/



    /***
     * 修改广告数据修改监听
     * 同步数据到Redis
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content" }, eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.INSERT,CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //获取广告分类的ID
        String categoryId = getColumn(rowData, "category_id");
        //根据广告分类ID获取所有广告
        Result<List<Content>> result = contentFeign.findByCategory(Long.valueOf(categoryId));
        //将广告数据存入到Redis缓存
        List<Content> contents = result.getData();
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(contents));
    }


    /***
     * 获取指定列的值
     * @param rowData
     * @param columnName
     * @return
     */
    public String getColumn(CanalEntry.RowData rowData,String columnName){
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            if(column.getName().equals(columnName)){
                return column.getValue();
            }
        }

        //有可能是删除操作
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            if(column.getName().equals(columnName)){
                return column.getValue();
            }
        }
        return null;
    }
}
