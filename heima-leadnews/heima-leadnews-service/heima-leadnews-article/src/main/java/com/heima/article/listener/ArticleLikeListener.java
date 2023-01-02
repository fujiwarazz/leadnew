package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.model.article.entity.ApArticle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.nntp.Article;
import org.checkerframework.checker.units.qual.A;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 01/01/2023 21:54
 */
@Slf4j
@Component
public class ArticleLikeListener {

    @Resource
    private ApArticleService apArticleService;

    @KafkaListener(topics = ArticleConstants.ARTICLE_LIKE_CHANGE_TOPIC)
    public void changeLikeStatus(String msg){
        Map map = JSON.parseObject(msg, Map.class);
        Long articleId = (Long) map.get("articleId");
        Boolean flag = (Boolean) map.get("isMember");
        if(flag){
             apArticleService.update().setSql("likes = likes-1").eq("id",articleId).update();
        }else{
            apArticleService.update().setSql("likes = likes+1").eq("id",articleId).update();
        }
    }
    @KafkaListener(topics = ArticleConstants.ARTICLE_READ_INCRE)
    public void increRead(String msg){
        apArticleService.update().setSql("views = views +1").eq("id",msg);
    }

}
