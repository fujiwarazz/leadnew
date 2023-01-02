package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.wemedia.entity.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author peelsannaw
 */
@Service
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    @Override
    public ResponseResult<?> getChannels() {
        List<WmChannel> list = this.list();
        return ResponseResult.okResult(list);
        
    }

}