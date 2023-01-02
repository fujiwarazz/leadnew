package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.entity.AdUser;
import com.heima.common.common.dtos.ResponseResult;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:03
 */
public interface AdUserService extends IService<AdUser> {


    ResponseResult<?> authLogin(AdUserDto adUserDto);
}
