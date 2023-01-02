package com.heima.user.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.search.entity.mongo.ApUserSearch;
import com.heima.model.user.dto.UserFollowDto;
import com.heima.user.service.ApUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 02/01/2023 10:46
 */
@RestController
@RequestMapping("/api/v1/user/")
public class UserFollowController {

    @Resource
    ApUserService apUserService;

    @PostMapping("user_follow")
    public ResponseResult<?>userFollow(@RequestBody UserFollowDto dto){
        return apUserService.userFollow(dto);
    }
}
