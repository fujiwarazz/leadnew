package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.apUser.dto.LoginDto;
import com.heima.model.apUser.entity.ApUser;
import com.heima.model.apUser.vo.LoginVo;
import com.heima.model.user.dto.UserFollowDto;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:57
 */
public interface ApUserService extends IService<ApUser> {
    /**
     * 登录
     * @param loginDto
     * @return
     */
    LoginVo authLogin(LoginDto loginDto);

    ResponseResult<?> userFollow(UserFollowDto dto);
}
