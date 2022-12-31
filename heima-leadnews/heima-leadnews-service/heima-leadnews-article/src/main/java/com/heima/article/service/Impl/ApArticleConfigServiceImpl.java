package com.heima.article.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Author peelsannaw
 * @create 30/12/2022 13:34
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {

    @Override
    public void updateByMap(Map map) {
        Object articleId = map.get("articleId");
        boolean flag = true;
        if(map.get("enable").equals(1)){
            flag = false;
        }
        log.info(this.getById(articleId.toString()).toString());
        this.update(Wrappers.<ApArticleConfig>lambdaUpdate().set(ApArticleConfig::getIsDown,flag).eq(ApArticleConfig::getId,articleId));
    }
}
