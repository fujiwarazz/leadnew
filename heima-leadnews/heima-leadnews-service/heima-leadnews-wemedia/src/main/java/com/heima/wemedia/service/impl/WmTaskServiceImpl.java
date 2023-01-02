package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.schedule.dto.Task;
import com.heima.model.wemedia.TaskTypeEnum;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewAutoVerification;
import com.heima.wemedia.service.WmTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author peelsannaw
 * @create 28/12/2022 20:30
 */
@Service
@Slf4j
public class WmTaskServiceImpl implements WmTaskService {

    @Resource
    private IScheduleClient client;
    @Resource
    private WmNewAutoVerification autoVerification;

    @Async
    @Override
    public void addNewsToTask(Integer id, Date pubDate) {
        log.info("添加任务至延迟 开始");
        Task task = new Task();
        task.setExecuteTime(pubDate.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));
        client.addTask(task);
        log.info("添加任务至延迟 结束");
    }

    @Override
    @Scheduled(cron = "*/3 * * * * ?")
    public void verifyTask() {
        //找到文章定时审核的task
        log.info("审核文章开始");
        ResponseResult<?> poll = client.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        if(poll.getCode().equals(200) && poll.getData()!=null){
            Task task = JSON.parseObject(JSON.toJSONString(poll.getData()), Task.class);
            Integer deserializeId = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class).getId();
            autoVerification.autoVerification(deserializeId);
        }
        log.info("审核文章结束");
    }

}
