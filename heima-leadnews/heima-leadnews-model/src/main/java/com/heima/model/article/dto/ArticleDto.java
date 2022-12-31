package com.heima.model.article.dto;

import com.heima.model.article.entity.ApArticle;
import lombok.Data;

/**
 * @Author peelsannaw
 * @create 13/11/2022 下午3:48
 */
@Data
public class ArticleDto extends ApArticle {
    private String content;
}
