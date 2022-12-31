package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleFallback;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author peelsannaw
 * @create 13/11/2022 下午3:53
 */
@FeignClient(value = "leadnews-article",fallback = IArticleFallback.class)
public interface IArticleClient{

    /**
     * ...
     * @param articleDto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult<?> saveArticle(@RequestBody ArticleDto articleDto);
}
