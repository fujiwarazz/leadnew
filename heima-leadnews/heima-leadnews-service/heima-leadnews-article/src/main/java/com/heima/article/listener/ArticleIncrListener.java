package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.model.message.ArticleVisitStreamMess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 01/01/2023 21:54
 */
@Slf4j
@Component
public class ArticleIncrListener {

    @Resource
    private ApArticleService apArticleService;

    @KafkaListener(topics = ArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void changeLikeStatus(String msg){
        ArticleVisitStreamMess articleVisitStreamMess = JSON.parseObject(msg, ArticleVisitStreamMess.class);
        apArticleService.updateHotArticleScore(articleVisitStreamMess);
    }

}
