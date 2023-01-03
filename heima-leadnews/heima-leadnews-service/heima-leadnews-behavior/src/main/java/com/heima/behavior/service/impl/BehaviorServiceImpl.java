package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.BehaviorService;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dto.LikesBehaviorDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.message.UpdateArticleMess;
import com.heima.utils.common.ApUserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 01/01/2023 20:46
 */
@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    KafkaTemplate<String,String>kafkaTemplate;

    @Override
    public ResponseResult<?> like(LikesBehaviorDto likesBehaviorDto) {
        if(likesBehaviorDto.getArticleId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        try {
            UpdateArticleMess updateArticleMess = new UpdateArticleMess();
            updateArticleMess.setArticleId(likesBehaviorDto.getArticleId());
            updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.LIKES);
                //从redis中判断是否点赞,同步更新数据库

            String key = ArticleConstants.ARTICLE_LIKE_PREFIX+ ApUserThreadLocal.getUser().getId();
            System.out.println(key);
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, likesBehaviorDto.getArticleId().toString());

//            Map<String ,Object> map = new Hashtable<>();
//            map.put("isMember",isMember);
//            map.put("articleId",likesBehaviorDto.getArticleId());
//            log.info("kafka推送消息");
           // kafkaTemplate.send(ArticleConstants.ARTICLE_LIKE_CHANGE_TOPIC,JSON.toJSONString(map));
//            log.info("kafka推送消息结束");


            if(Boolean.FALSE.equals(isMember)){
                updateArticleMess.setAdd(1);
                stringRedisTemplate.opsForSet().add(key,likesBehaviorDto.getArticleId().toString());
            }else{
                updateArticleMess.setAdd(-1);
                stringRedisTemplate.opsForSet().remove(key,likesBehaviorDto.getArticleId().toString());
            }
            //发送消息给stream
            log.info("kafka推送消息");
            kafkaTemplate.send(ArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));
            log.info("kafka推送消息结束");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();

    }

    @Override
    @SuppressWarnings("all")
    public ResponseResult<?> read(Map dto) {
        String articleId = (String) dto.get("articleId");
        Integer count = (Integer) dto.get("count");
        if(articleId==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String key = ArticleConstants.ARTICLE_READ_PREFIX + articleId;
        stringRedisTemplate.opsForValue().increment(key,1);
        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(Long.parseLong(articleId));
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        updateArticleMess.setAdd(1);
        kafkaTemplate.send(ArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));
        return ResponseResult.okResult();
    }

    /**
     * DISLIKE
     * @param map
     * @return
     */
    @Override
    public ResponseResult<?> changeLike(Map map) {
        String articleId = map.get("articleId").toString();
        String type = map.get("type").toString();
        String userId = ApUserThreadLocal.getUser().getId().toString();
        String key = ArticleConstants.ARTICLE_UNLIKE_PREFIX +userId ;
        if("0".equals(type)){
            stringRedisTemplate.opsForSet().add(key,articleId.toString());
        }else{
            stringRedisTemplate.opsForSet().remove(key,articleId.toString());
        }
        //推送消息更新

        return ResponseResult.okResult();
    }


}
