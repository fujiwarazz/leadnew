package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmLoginDto;
import com.heima.model.wemedia.entity.WmUser;


/**
 * @author peelsannaw
 */
public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

}