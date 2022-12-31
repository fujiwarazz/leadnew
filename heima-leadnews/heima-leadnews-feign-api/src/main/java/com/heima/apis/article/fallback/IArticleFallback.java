package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.common.annotation.LogEnhance;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @Author peelsannaw
 * @create 16/11/2022 下午6:29
 */
@Component
public class IArticleFallback implements IArticleClient {

    @LogEnhance(BusinessName = "文章feign")
    @Override
    public ResponseResult<?> saveArticle(ArticleDto articleDto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
