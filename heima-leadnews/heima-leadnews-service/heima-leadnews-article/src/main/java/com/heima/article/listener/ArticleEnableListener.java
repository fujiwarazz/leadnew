package com.heima.article.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.wm.WmConstants;
import com.heima.model.article.entity.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 30/12/2022 13:30
 */
@Component
@Slf4j
public class ArticleEnableListener {

    @Resource
    private ApArticleConfigService apArticleConfigService;

    @SuppressWarnings("all")
    @KafkaListener(topics = WmConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onMessage(String msg){
        if(StrUtil.isNotBlank(msg)){
            log.info("接收到消息:{}",JSON.parseObject(msg,Map.class));
            Map map = JSON.parseObject(msg, Map.class);
            apArticleConfigService.updateByMap(map);
        }
    }
}
