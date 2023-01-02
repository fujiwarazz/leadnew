package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.common.common.dtos.ResponseResult;

import java.util.Map;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午9:15
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    ResponseResult<?> load(Short loadtype, ArticleHomeDto dto);

    ResponseResult<?> saveArticleDTO(ArticleDto articleDto);

    ResponseResult<?> loadArticleBehavior(Map map);

    ResponseResult<?> userCollect(Map map);
}