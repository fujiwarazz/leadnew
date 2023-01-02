package com.heima.model.user.dto;

import com.heima.common.annotation.IdEncrypt;
import lombok.Data;
import lombok.Getter;

/**
 * @Author peelsannaw
 * @create 02/01/2023 10:49
 */
@Data
@Getter
public class UserFollowDto {

    @IdEncrypt
    private Long articleId;
    private Integer authorId;
    private Short operatoin;
}
