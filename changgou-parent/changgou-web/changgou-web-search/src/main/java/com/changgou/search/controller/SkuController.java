package com.changgou.search.controller;

import com.changgou.entity.Page;
import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/18
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Controller
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 搜索
     * @param searchMap
     * @param model
     * @return
     */
    @GetMapping(value="/list")
    public String search(@RequestParam(required = false) Map<String, String> searchMap, Model model){
        //替换特殊字符
        handlerSearchMap(searchMap);
        //搜索服务提供的数据
        Map<String, Object> resultmap = skuFeign.search(searchMap);
        //页面数据
        model.addAttribute("result",resultmap);
        //添加回显的搜索条件
        model.addAttribute("searchMap",searchMap);
        //组装URL
       String[] urls = geturl(searchMap);
       model.addAttribute("url",urls[0]);
       model.addAttribute("sorturl",urls[1]);

       //分页条件
        Page<SkuInfo> page = new Page(
                Long.parseLong(resultmap.get("totalElements").toString()),
                Integer.parseInt(resultmap.get("pageNum").toString()),
                Integer.parseInt(resultmap.get("pageSize").toString())
        );
        model.addAttribute("page",page);
        return "search";
    }

    /**
     * 抽取方法拼接搜索条件到url
     * @param searchMap
     * @return
     */
    private String[] geturl(Map<String, String> searchMap) {
        String url = "/search/list";
        String sorturl = "/search/list";
        if(searchMap != null && searchMap.size() > 0){
            url += "?";
            sorturl += "?";
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                //前段实现分页,无须拼接到url
                String key = entry.getKey();
                if(key.equals("pageNum")){
                    continue;
                }
                if(key.equals("sortRule") || key.equals("sortField")){
                    continue;
                }
                url += key+"="+entry.getValue()+"&";
                sorturl += key + "=" + entry.getValue() + "&";
            }
            //去掉最后一个&
            url = url.substring(0,url.length()-1);
            sorturl = sorturl.substring(0, sorturl.length()-1);
        }
        return new String[]{url, sorturl};
    }



    /****
     * 替换特殊字符
     * @param searchMap
     */
    public void handlerSearchMap(Map<String,String> searchMap){
        if(searchMap!=null){
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                if(entry.getKey().startsWith("spec_")){
                    entry.setValue(entry.getValue().replace("+","%2B"));
                }
            }
        }
    }
}
