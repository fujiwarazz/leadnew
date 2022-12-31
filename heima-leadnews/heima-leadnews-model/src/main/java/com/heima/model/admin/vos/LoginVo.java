package com.heima.model.admin.vos;

import com.heima.model.admin.dtos.LoginDto;
import com.heima.model.apUser.entity.ApUser;
import lombok.Data;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:54
 */
@Data
public class LoginVo {

    private LoginDto user;
    private String token;
}
