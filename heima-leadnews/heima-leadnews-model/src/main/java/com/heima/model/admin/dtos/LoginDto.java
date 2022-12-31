package com.heima.model.admin.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:55
 */
@Data
public class LoginDto {

    private Integer id;
    private String name;
    private String nickname;
    private String image;
    private String phone;
    private Integer status;
    private Date loginTime;
    private Date createdTime;
}
