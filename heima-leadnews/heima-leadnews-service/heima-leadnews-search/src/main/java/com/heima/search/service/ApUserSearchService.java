package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

import java.util.Map;

/**
 * @Author peelsannaw
 * @create 31/12/2022 18:10
 */

public interface ApUserSearchService {


    /**
     * 新增搜索记录
     * @param KeyWord 关键词
     * @param apUserId 登录用户
     */
    public void insertSearchHistory(String KeyWord,Integer apUserId);

    /**
     * 历史记录
     * @return
     */
    ResponseResult<?> getUserSearchHistory();

    ResponseResult<?> delUserSearchHistory(Map request);
}
