package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmNewsDto;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.wemedia.service.WmChannelService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 12/11/2022 下午8:27
 */
@RestController
@RequestMapping("/api/v1/channel")

public class WmChannelsController {

    @Resource
    private WmChannelService channelService;

    @GetMapping("channels")
    public ResponseResult<?> getChannels(){
        return channelService.getChannels();
    }


}
