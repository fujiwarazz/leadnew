package com.heima.search.service.Impl;

import com.heima.search.service.ApUserSearchService;
import com.heima.utils.common.ApUserThreadLocal;
import net.sf.jsqlparser.expression.BinaryExpression;
import org.apache.avro.io.BinaryDecoder;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.search.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author peelsannaw
 * @create 30/12/2022 23:15
 */
@Slf4j
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ApUserSearchService apUserSearchService;


    public static final String APP_SEARCH_INDEX = "app_info_article";
    @Override
    public ResponseResult<?> search(UserSearchDto dto) throws IOException {
//        //检查参数
//        String searchWords = userSearchDto.getSearchWords();
//        if(searchWords==null){
//            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
//        }
//        //准备request
//        SearchRequest searchRequest = new SearchRequest(APP_SEARCH_INDEX);
//        //准备dsl
//        setBooleanDsl(searchRequest,userSearchDto);
//        //发送请求
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        //处理相应 封装结果
//        return handleResponse(searchResponse);

        //1.检查参数
        if(dto == null || StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //异步调用保存搜索记录到mongo
        if(dto.getFromIndex()==0 && ApUserThreadLocal.getUser()!=null){
            apUserSearchService.insertSearchHistory(dto.getSearchWords(), ApUserThreadLocal.getUser().getId());
        }
        //2.设置查询条件
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //关键字的分词之后查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(dto.getSearchWords()).field("title").field("content").defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder);

        //查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);

        //分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(dto.getPageSize());

        //按照发布时间倒序查询
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        //设置高亮  title
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        //这里为false因为我match的和需要高亮的field不是同一个所以不需要匹配
        searchSourceBuilder.highlighter(highlightBuilder);


        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


        //3.结果封装返回

        List<Map> list = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Map map = JSON.parseObject(json, Map.class);
            //处理高亮
            if(hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0){
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StringUtils.join(titles);
                //高亮标题
                map.put("h_title",title);
            }else {
                //原始标题
                map.put("h_title",map.get("title"));
            }
            list.add(map);
        }

        return ResponseResult.okResult(list);

    }

    @SuppressWarnings("all")
    private ResponseResult<?> handleResponse(SearchResponse searchResponse) {
        List<Map> list = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Map map = JSON.parseObject(json, Map.class);
            //处理高亮
            if(hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0){
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StringUtils.join(titles);
                //高亮标题
                map.put("h_title",title);
            }else {
                //原始标题
                map.put("h_title",map.get("title"));
            }
            list.add(map);
        }
        return ResponseResult.okResult(list);
    }

    private void setBooleanDsl(SearchRequest searchRequest, UserSearchDto userSearchDto) {
        //设置条件 关键词,小于时间,分页,按照发布desc,高亮
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",userSearchDto.getSearchWords()));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(userSearchDto.getFromIndex());
        searchSourceBuilder.size(userSearchDto.getPageSize());
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);
        searchSourceBuilder.highlighter(new HighlightBuilder()
                .field("title")
                .preTags("<font style='color: red; font-size: inherit;'>")
                .postTags("</font>"));
        searchRequest.source(searchSourceBuilder);
    }


}
