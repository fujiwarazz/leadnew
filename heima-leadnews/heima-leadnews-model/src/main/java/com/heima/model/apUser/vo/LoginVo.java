package com.heima.model.apUser.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午11:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {
    private LoginUserVo user;
    private String token;
    private Boolean isGuest;
}
