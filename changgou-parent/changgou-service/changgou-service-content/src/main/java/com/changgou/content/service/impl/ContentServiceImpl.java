package com.changgou.content.service.impl;

import com.changgou.content.dao.ContentMapper;
import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author MartinMYZ
 * @description 广告内容实现类
 * @created at 2019/8/14
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired(required = false)
    private ContentMapper contentMapper;

    @Override
    public List<Content> findByCategory(Long id) {
        Content content = new Content();
        content.setCategoryId(id);
        content.setStatus("1");
        List<Content> list = contentMapper.select(content);
        return list;
    }
}
