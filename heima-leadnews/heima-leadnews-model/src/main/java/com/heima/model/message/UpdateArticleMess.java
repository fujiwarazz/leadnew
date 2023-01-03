package com.heima.model.message;

import lombok.Data;

/**
 * @Author peelsannaw
 * @create 03/01/2023 14:54
 */

@Data
public class UpdateArticleMess {

    /**
     * 修改文章的字段类型
     */
    private UpdateArticleType type;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 修改数据的增量，可为正负
     */
    private Integer add;

    /**
     * 行为信息
     */
    public enum UpdateArticleType{
        COLLECTION,
        COMMENT,
        LIKES,
        VIEWS;
    }
}