package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/15
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 将数据导入es服务器中
     */
    @Override
    public void importSku() {
        //通过feign调用商品微服务,查询结果
        Result<List<Sku>> skuList = skuFeign.findByStatus("1");
        //将结果通过fastjson API转换成skuinfo进行封装
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList.getData()), SkuInfo.class);
        //处理动态字段
        for (SkuInfo skuInfo : skuInfoList) {
            String spec = skuInfo.getSpec();
            Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }


    /**
     * 搜索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //封装检索条件--单独抽取一个方法
        NativeSearchQueryBuilder builder = queryBasic(searchMap);
        //将检索的数据封装到map中
        //根据条件查找,获取该关键字下的检索信息
        Map<String, Object> resultMap = searchByKeyword(builder);
        // 商品分类, 品牌列表,规格列表
        //商品分类
        /*List<String> categoryList = searchCategoryList(builder);
        resultMap.put("categoryList",categoryList);
        //品牌列表
        List<String> brandList = searchBrandList(builder);
        resultMap.put("brandList",brandList);
        //规格列表
        Integer totalElements = Integer.parseInt(resultMap.get("totalElements").toString());
        if(totalElements <= 0){
            totalElements = 10000;
        }
        Map<String,Set<String>> specMap = searchSpecMap(builder, totalElements);
        resultMap.put("SpecMap",specMap);*/
        Map<String, Object> map = searchGroupList(builder);
        resultMap.putAll(map);
        return resultMap;
    }

    //START******************优化后***************************************************//
    private Map<String, Object> searchGroupList(NativeSearchQueryBuilder builder) {
        //分组查询(聚合查询)
        //terms别名
        builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> pages = esTemplate.queryForPage(builder.build(), SkuInfo.class);

        List<String> brandList = getList(pages, "skuBrand");
        List<String> categoryList = getList(pages, "skuCategory");
        List<String> specList = getList(pages, "skuSpec");

        Map<String, Set<String>> specMap = specPutAll(specList);
        Map<String, Object> map = new HashMap<>();
        map.put("brandList", brandList);
        map.put("categoryList", categoryList);
        map.put("specList", specMap);
        return map;
    }

    private List<String> getList(AggregatedPage<SkuInfo> pages, String groupName) {
        StringTerms stringTerms = pages.getAggregations().get(groupName);
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //封装商品分类的list
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }


    /**
     * 规格信息转换为map 将List<String>转换成Map<String, Set<String>>
     *
     * @param list
     * @return
     */
    private Map<String, Set<String>> specPutAll(List<String> list) {
        Map<String, Set<String>> specMap = new HashMap<>();//封装规格数据
        for (String specString : list) {
            Map<String, String> map = JSON.parseObject(specString, Map.class);
            for (Map.Entry<String, String> stringEntry : map.entrySet()) {
                String key = stringEntry.getKey();
                String value = stringEntry.getValue();
                //判断map中是否有set
                Set<String> stringSet = specMap.get(key);
                if (stringSet == null) {
                    stringSet = new HashSet<>();
                }
                stringSet.add(value);
                specMap.put(key, stringSet);
            }
        }
        return specMap;
    }
//END******************优化后***************************************************//


//**START*******************优化前的方法(品牌表和分类表以及规格表)*******************************************************************************//

    /**
     * 规格列表搜索查询
     *
     * @param builder
     * @return
     */
    private Map<String, Set<String>> searchSpecMap(NativeSearchQueryBuilder builder, Integer totalElements) {
        //分组查询(聚合查询)
        //terms别名// size() 加载每页的数据的条数

        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(totalElements));
        AggregatedPage<SkuInfo> pages = esTemplate.queryForPage(builder.build(), SkuInfo.class);

        StringTerms stringTerms = pages.getAggregations().get("skuSpec");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //封装商品分类的list
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        Map<String, Set<String>> specMap = specPutAll(list);

        return specMap;
    }

    /**
     * 根据封装条件查询品牌列表
     * @param builder
     * @return
     */
   /* private List<String> searchBrandList(NativeSearchQueryBuilder builder) {
        //分组查询(聚合查询)
        //terms别名
        builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        AggregatedPage<SkuInfo> pages = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        StringTerms stringTerms= pages.getAggregations().get("skuBrand");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //封装商品分类的list
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    *//**
     * 根据封装条件查询分类名称
     * @param builder
     * @return
     *//*
    private List<String> searchCategoryList(NativeSearchQueryBuilder builder) {
        //分组查询(聚合查询)
        //terms别名
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> pages = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        StringTerms stringTerms= pages.getAggregations().get("skuCategory");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //封装商品分类的list
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }*/
//**END*******************优化前的方法(品牌表和分类表)*******************************************************************************//

    /**
     * 根据关键字条件进行检索查询
     *
     * @param builder
     * @return
     */
    private Map<String, Object> searchByKeyword(NativeSearchQueryBuilder builder) {
        //关键字高亮显示
        //构建高亮检索条件
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");//设置高亮条件
        field.preTags("<font color=red>");
        field.postTags("</font>");
        //field.fragmentSize(100); 默认即100, 显示数据的字符个数
        builder.withHighlightFields(field);
        //
        SearchResultMapper searchResultMapper = new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //封装高亮结果集
                List<T> content = new ArrayList<>();
                //获取高亮结果集
                SearchHits hits = response.getHits();
                if (hits != null) {
                    for (SearchHit hit : hits) {
                        String result = hit.getSourceAsString();//获取普通结果集
                        //将结果转成Pojo
                        SkuInfo skuInfo = JSON.parseObject(result, SkuInfo.class);
                        HighlightField highlightField = hit.getHighlightFields().get("name");
                        if (highlightField != null) {
                            Text[] texts = highlightField.getFragments(); //获取高亮结果集
                            skuInfo.setName(texts[0].toString()); //替换普通结果
                        }
                        content.add((T) skuInfo);
                    }
                }
                return new AggregatedPageImpl<>(content, pageable, hits.getTotalHits());
            }
        };

        NativeSearchQuery nativeSearchQuery = builder.build();
        AggregatedPage<SkuInfo> pages = esTemplate.queryForPage(nativeSearchQuery, SkuInfo.class, searchResultMapper);//多一个参数,高亮
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", pages.getContent()); //结果集
        resultMap.put("totalElements", pages.getTotalElements()); //商品总条数
        resultMap.put("totalPages", pages.getTotalPages()); //总页数
        resultMap.put("pageNum", pages.getPageable().getPageNumber() + 1); //添加当前页
        resultMap.put("pageSize", pages.getPageable().getPageSize()); //
        return resultMap;
    }

    /**
     * 封装检索条件的方法
     *
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder queryBasic(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //构建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (searchMap != null) {
            //1.根据关键字检索
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                builder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
            //2.根据商品分类检索
            String category = searchMap.get("category");
            if (!StringUtils.isEmpty(category)) {
                queryBuilder.must(QueryBuilders.matchQuery("categoryName", category));
            }
            //3.根据商品品牌检索
            String brand = searchMap.get("brand");
            if (!StringUtils.isEmpty(brand)) {
                queryBuilder.must(QueryBuilders.matchQuery("brandName", brand));
            }
            //4.根据商品规格检索(规格可能有多个) 检索条件为: specMap.XXX.keyword
            Set<String> keySet = searchMap.keySet();
            for (String key : keySet) {
                if (key.startsWith("spec_")) {
                    String value = searchMap.get(key).replace("\\","");
                    queryBuilder.must(QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword", value));
                }
            }
            //5. 根据商品价格区间检索
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                String[] priceArray = price.split("-");
                queryBuilder.must(QueryBuilders.rangeQuery("price").gte(priceArray[0]));
                //针对区间段来搜索
                if (priceArray.length == 2) {
                    queryBuilder.must(QueryBuilders.rangeQuery("price").lte(priceArray[1]));
                }
            }
            //6 排序, 根据哪个字段排序, 排序规则(升序,降序)
            String sortRule = searchMap.get("sortRule");//排序规则
            String sortField = searchMap.get("sortField"); //排序字段
            if (!StringUtils.isEmpty(sortField)) {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }
        }
        //将过滤条件封装到builder对象中
        builder.withFilter(queryBuilder);
        //添加分页条件
        String pageNum = searchMap.get("pageNum");
        //设置默认页数
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = "1";
        }
        int page = Integer.parseInt(pageNum);
        int size = 10; //每页显示的条数
        Pageable pageable = PageRequest.of(page - 1, size); //当前页面
        builder.withPageable(pageable);
        return builder;
    }
}
