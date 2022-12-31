package com.heima.model.apUser.dto;

import lombok.Data;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:48
 */
@Data
public class LoginDto {

    private String phone;

    private String code;

    private String password;
}
