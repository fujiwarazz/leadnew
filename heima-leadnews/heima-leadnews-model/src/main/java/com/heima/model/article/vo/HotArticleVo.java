package com.heima.model.article.vo;

import com.heima.model.article.entity.ApArticle;
import lombok.Data;
import lombok.Getter;

/**
 * @Author peelsannaw
 * @create 02/01/2023 19:22
 */
@Data
@Getter
public class HotArticleVo extends ApArticle {
    private Integer score;
}
