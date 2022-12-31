package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.entity.ApArticleConfig;

import java.util.Map;

/**
 * @Author peelsannaw
 * @create 30/12/2022 13:34
 */
public interface ApArticleConfigService extends IService<ApArticleConfig> {
    void updateByMap(Map map);
}
