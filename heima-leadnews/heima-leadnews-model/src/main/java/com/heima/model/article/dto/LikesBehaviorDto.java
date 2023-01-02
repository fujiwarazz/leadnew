package com.heima.model.article.dto;


import com.heima.common.annotation.IdEncrypt;
import lombok.Data;

/**
 * @Author peelsannaw
 * @create 01/01/2023 20:51
 */
@Data
public class LikesBehaviorDto {
    @IdEncrypt
    private Long articleId;
    //0 点赞   1 取消点赞
    private Short operation;
    //0文章  1动态   2评论
    private Short type;
}
