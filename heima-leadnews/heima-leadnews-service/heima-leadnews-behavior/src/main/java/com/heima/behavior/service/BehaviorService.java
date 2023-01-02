package com.heima.behavior.service;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.article.dto.LikesBehaviorDto;

import java.util.Map;

/**
 * @Author peelsannaw
 * @create 01/01/2023 20:44
 */
public interface BehaviorService {
    ResponseResult<?> like(LikesBehaviorDto likesBehaviorDto);

    ResponseResult<?> read(Map dto);

    ResponseResult<?> changeLike(Map map);
}
