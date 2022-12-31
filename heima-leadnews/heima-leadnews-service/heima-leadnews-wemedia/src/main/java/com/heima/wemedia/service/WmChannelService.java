package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmNewsDto;
import com.heima.model.wemedia.entity.WmChannel;

/**
 * @author peelsannaw
 */
public interface WmChannelService extends IService<WmChannel> {


    ResponseResult<?> getChannels();


}