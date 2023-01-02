package com.heima.user.controller.v1;

import com.heima.common.exception.CustomException;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.apUser.dto.LoginDto;
import com.heima.model.apUser.vo.LoginVo;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:53
 */
@RestController
@RequestMapping("/api/v1/login")
@Api(value = "app端口用户登录controller")
public class UserLoginController {

    @Resource
    private ApUserService apUserService;

    @PostMapping("login_auth")
    public ResponseResult<?>userLoginAuth(@RequestBody LoginDto loginDto){
        try {
            LoginVo loginVo = apUserService.authLogin(loginDto);
            return ResponseResult.okResult(loginVo);
        } catch (CustomException e) {
            return ResponseResult.errorResult(e.getAppHttpCodeEnum());
        }
    }
}
