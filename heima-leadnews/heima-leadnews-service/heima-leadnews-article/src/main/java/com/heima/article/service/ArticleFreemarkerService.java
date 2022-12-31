package com.heima.article.service;

import com.heima.model.article.entity.ApArticle;

/**
 * @Author peelsannaw
 * @create 16/12/2022 下午9:28
 */

public interface ArticleFreemarkerService {
    /**
     * 生成静态文件
     * @param apArticle
     * @param content
     */
    String buildArticleToStatic(ApArticle apArticle,String content);
}
