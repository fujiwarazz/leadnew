package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.BehaviorService;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dto.LikesBehaviorDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.utils.common.ApUserThreadLocal;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 01/01/2023 20:46
 */
@Service
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
            //从redis中判断是否点赞,同步更新数据库
            System.out.println(ApUserThreadLocal.getUser().getId());
            String key = ArticleConstants.ARTICLE_LIKE_PREFIX+ ApUserThreadLocal.getUser().getId();
            System.out.println(key);
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, likesBehaviorDto.getArticleId().toString());
            Map<String ,Object> map = new Hashtable<>();
            map.put("isMember",isMember);
            map.put("articleId",likesBehaviorDto.getArticleId());
            kafkaTemplate.send(ArticleConstants.ARTICLE_LIKE_CHANGE_TOPIC,JSON.toJSONString(map));
            if(Boolean.FALSE.equals(isMember)){
                stringRedisTemplate.opsForSet().add(key,likesBehaviorDto.getArticleId().toString());
            }else{
                stringRedisTemplate.opsForSet().remove(key,likesBehaviorDto.getArticleId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();

    }

    @Override
    @Async
    public ResponseResult<?> read(Map dto) {
        Long articleId = (Long) dto.get("articleId");
        Integer count = (Integer) dto.get("count");
        if(articleId==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String key = ArticleConstants.ARTICLE_READ_PREFIX + articleId;
        stringRedisTemplate.opsForValue().increment(key,1);
        kafkaTemplate.send(ArticleConstants.ARTICLE_READ_INCRE,articleId.toString());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<?> changeLike(Map map) {
        Integer articleId = (Integer) map.get("articleId");
        Short type = (Short) map.get("type");
        String key = ArticleConstants.ARTICLE_UNLIKE_PREFIX + articleId;
        if(type==0){
            stringRedisTemplate.opsForSet().add(key,articleId.toString());
        }else{
            stringRedisTemplate.opsForSet().remove(key,articleId.toString());
        }
        return ResponseResult.okResult();
    }
}
