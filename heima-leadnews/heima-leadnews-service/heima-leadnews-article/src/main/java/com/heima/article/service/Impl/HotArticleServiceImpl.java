package com.heima.article.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.vo.HotArticleVo;
import jodd.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author peelsannaw
 * @create 02/01/2023 19:08
 */
@Service
@Slf4j
public class HotArticleServiceImpl implements HotArticleService {


    @Resource
    private ApArticleMapper apArticleMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void computeHotArticleScore() {
        //查询前五天的所有文章 joda
        Date date = DateTime.now().minusDays(5).toDate();
        List<ApArticle> hotArticles = apArticleMapper.getArticlesFromFiveDayBefore(date);
        //计算它们的score
        List<HotArticleVo> hotArticleVos = computeArticle(hotArticles);
        //按照channel分组
        Map<Integer, List<HotArticleVo>> collect = hotArticleVos.stream().collect(Collectors.groupingBy(ApArticle::getChannelId));
        //每个分组求出前30条
        collect.forEach((k, v) -> {
            //排序,存入redis
            List<HotArticleVo> groupHots = v.stream()
                    .sorted((o1, o2) -> o2.getScore() - o1.getScore()).collect(Collectors.toList());
            if (groupHots.size() >= 30) {
                groupHots = groupHots.subList(0, 30);
            }
            cacheToRedis(k, JSON.toJSONString(groupHots));
        });
        //推荐总的前30条
        if (hotArticleVos.size() >= 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        hotArticleVos = hotArticleVos.stream().sorted((o1, o2) -> o2.getScore() - o1.getScore()).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE+ArticleConstants.DEFAULT_TAG,JSON.toJSONString(hotArticleVos));
    }

    private void cacheToRedis(Integer k, String v) {
        String key = ArticleConstants.HOT_ARTICLE_FIRST_PAGE + k;
        stringRedisTemplate.opsForValue().set(key,v);
    }

    private List<HotArticleVo> computeArticle(List<ApArticle> hotArticles) {
        return hotArticles.stream().filter(Objects::nonNull).map(item -> {
            HotArticleVo hotArticleVo = BeanUtil.copyProperties(item, HotArticleVo.class);
            hotArticleVo.setScore(computeArticleScore(item));
            return hotArticleVo;
        }).collect(Collectors.toList());
    }

    private Integer computeArticleScore(ApArticle item) {
        int score = 0;
        if (item.getLikes() != null) {
            score += item.getLikes() * ArticleConstants.ARTICLE_LIKE_WEIGHT;
        }
        if (item.getCollection() != null) {
            score += item.getCollection() * ArticleConstants.ARTICLE_COLLECT_WEIGHT;
        }
        if (item.getViews() != null) {
            score += item.getViews() * ArticleConstants.ARTICLE_VIEW_WEIGHT;
        }
        if (item.getComment() != null) {
            score += item.getComment() * ArticleConstants.ARTICLE_COMMENT_WEIGHT;
        }
        return score;
    }
}
