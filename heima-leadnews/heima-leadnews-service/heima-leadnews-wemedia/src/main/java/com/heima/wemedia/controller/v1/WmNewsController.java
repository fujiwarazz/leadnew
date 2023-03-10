package com.heima.wemedia.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmNewsUpdateDto;
import com.heima.model.wemedia.dto.WmNewsDto;
import com.heima.model.wemedia.dto.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 12/11/2022 下午8:35
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Resource
    private WmNewsService wmNewsService;
    @PostMapping("/list")
    public ResponseResult<?> findAll(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.findAll(dto);

    }
    @PostMapping("submit")
    public ResponseResult<?> submitNews(@RequestBody WmNewsDto wmNewsDto){
        return wmNewsService.submitNews(wmNewsDto);
    }

    @PostMapping("down_or_up")
    public ResponseResult<?> downOrUp(@RequestBody WmNewsUpdateDto wmnewsUpdateDto){
        return wmNewsService.downOrUp(wmnewsUpdateDto);
    }


}

