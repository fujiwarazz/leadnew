package com.heima.behavior.controller.v1;

import com.heima.behavior.service.BehaviorService;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.article.dto.LikesBehaviorDto;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 01/01/2023 20:33
 */
@RestController
@RequestMapping("/api/v1/")
public class ApUserBehaviorController {

    @Resource
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult<?> like(@RequestBody LikesBehaviorDto likesBehaviorDto){

        return behaviorService.like(likesBehaviorDto);
    }

    @PostMapping("/read_behavior")
    public ResponseResult<?> read(@RequestBody Map dto){
        return behaviorService.read(dto);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult<?> changeLike(Map map){
        return behaviorService.changeLike(map);
    }
}
