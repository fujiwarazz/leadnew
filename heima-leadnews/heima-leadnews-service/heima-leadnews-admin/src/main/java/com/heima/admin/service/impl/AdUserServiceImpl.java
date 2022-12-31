package com.heima.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.common.annotation.Prevent;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.dtos.LoginDto;
import com.heima.model.admin.entity.AdUser;
import com.heima.model.admin.vos.LoginVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AdminThreadLocalUtil;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.MD5Utils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:04
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    final String TOKEN_PREFIX = "admin_login:";

    @Prevent
    @Override
    public ResponseResult<?> authLogin(AdUserDto adUserDto) {

        if (adUserDto.getName() ==null|| adUserDto.getPassword() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        AdUser user = this.getOne(Wrappers.lambdaQuery(AdUser.class).eq(AdUser::getName,adUserDto.getName()));
        if(user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        String encodePwd = MD5Utils.encode(adUserDto.getPassword()+user.getSalt());
        if(adUserDto.getPassword().equals(encodePwd)){
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        String token = AppJwtUtil.getToken(user.getId().longValue());
        stringRedisTemplate.opsForValue().set(TOKEN_PREFIX+token, JSON.toJSONString(user),15, TimeUnit.DAYS);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setUser(BeanUtil.copyProperties(user, LoginDto.class));
        return ResponseResult.okResult(loginVo);
    }
}
