package com.changgou.content.service;

import com.changgou.content.pojo.Content;

import java.util.List;

public interface ContentService {

    /**
     * 根据categoryId查询广告集合
     * @param id
     * @return
     */
    List<Content> findByCategory(Long id);
}
