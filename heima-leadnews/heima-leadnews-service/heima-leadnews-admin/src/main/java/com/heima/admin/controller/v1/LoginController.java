package com.heima.admin.controller.v1;

import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.common.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 01/01/2023 13:35
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private AdUserService adUserService;

    @PostMapping("/in")
    public ResponseResult<?> adminLogin(@RequestBody AdUserDto adUserDto){
        return  adUserService.authLogin(adUserDto);
    }

}
