package com.heima.wemedia.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmLoginDto;
import com.heima.wemedia.service.WmUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author peelsannaw
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult<?> login(@RequestBody WmLoginDto dto){
        return wmUserService.login(dto);
    }
}
