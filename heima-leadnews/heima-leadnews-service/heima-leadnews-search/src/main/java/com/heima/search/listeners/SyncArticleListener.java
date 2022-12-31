package com.heima.search.listeners;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author peelsannaw
 * @create 31/12/2022 16:03
 */
@Component
@Slf4j
public class SyncArticleListener {

    @Resource
    RestHighLevelClient restHighLevelClient;
    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String msg) throws IOException {
        log.info("message = {}",msg);
        SearchArticleVo searchArticleVo = JSON.parseObject(msg, SearchArticleVo.class);
        //准备Request
        IndexRequest request = new IndexRequest("app_info_article");
        request.id(searchArticleVo.getId().toString());
        request.source(JSON.toJSONString(searchArticleVo), XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }
}
