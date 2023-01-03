package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 02/01/2023 22:05
 */
@Component
@Slf4j
public class ComputeHotArticleJob {

    @Resource
    private HotArticleService hotArticleService;

    @XxlJob("computeHotArticleJob")
    public void handleHotArticleJob(){
        //分片的参数

        log.info("热文章分值开始计算...任务开始执行....");
        hotArticleService.computeHotArticleScore();
        log.info("热文章分值开始计算...任务开始完毕....");
    }
}
