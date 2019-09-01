package com.changgou.search.service;

import java.util.Map;

public interface SkuService {
    /**
     * 导入sku数据
     */
    void importSku();

    /**
     * 搜索数据(前台业务检索)
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String,String> searchMap);
}
