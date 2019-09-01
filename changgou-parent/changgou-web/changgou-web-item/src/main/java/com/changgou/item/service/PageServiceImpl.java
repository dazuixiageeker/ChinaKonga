package com.changgou.item.service;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/19
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    //注入静态页面路径
    @Value("${pagepath}")
    private String pagepath;
    /**
     * 生成静态页
     * @param spuId
     */
    @Override
    public void createHtml(Long spuId) {

        try {
            //1. 构建数据模型
            Map<String, Object> dataModel = new HashMap<>();
            dataModel = getDataModel(spuId);

            Context context = new Context();
            context.setVariables(dataModel);
            //2. 指定生成路径
            File dir = new File(pagepath);
            if ( !dir.exists()){
                dir.mkdirs(); //目录为空,创建目录
            }
            //指定在什么目录下,创建什么名称的文件
            File destination = new File( dir, spuId+".html");
            //3. 利用模板组装,合成静态页
            PrintWriter printWriter = new PrintWriter(destination,"UTF-8");
            templateEngine.process("item", context, printWriter);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模板所需的数据
     * @param spuId
     * @return
     */
    private Map<String, Object> getDataModel(Long spuId) {
        Map<String, Object> dataModel = new HashMap<>();
        //spu商品信息
        Result<Spu> spuResult = spuFeign.findById(spuId);
        Spu spu = spuResult.getData();
        dataModel.put("spu",spu);
        //sku库存信息
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        Result<List<Sku>> skuresult = skuFeign.findList(sku);
        List<Sku> skuList = skuresult.getData();
        dataModel.put("skuList",skuList);
        //商品分类信息
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        dataModel.put("category1",category1);
        dataModel.put("category2",category2);
        dataModel.put("category3",category3);
        //商品小图
        String[] images = spu.getImages().split(",");
        dataModel.put("images",images);
        //商品规格
        Map<String, String> specificationMap = JSON.parseObject(spu.getSpecItems(), Map.class);
        dataModel.put("specificationMap",specificationMap);
        return dataModel;
    }
}
