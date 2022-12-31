package com.heima.schedule.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.schedule.ScheduleConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.redis.CacheService;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dto.Task;
import com.heima.model.schedule.entity.Taskinfo;
import com.heima.model.schedule.entity.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class TaskServiceImpl implements TaskService {


    @Resource
    private TaskinfoMapper taskinfoMapper;

    @Resource
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Resource
    private CacheService cacheService;

    @Resource
    private RedissonClient redissonClient;
    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Long addTask(Task task) {
        //1 添加到db
        boolean res1 = addTaskToDb(task);
        if (!res1) {
            //抛异常是为了回滚数据
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        //2 添加到redis
        boolean res2 = addTaskToRedis(task);
        if (!res2) {
            //抛异常是为了回滚数据
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        return task.getTaskId();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean cancel(String taskId) {
        //删除任务，更新tasklog表格
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);
        //从redis中删除
        if (task == null) {
            System.out.println("here");
            throw new RuntimeException("task 为空");
        }
        removeRedisTask(task);
        return true;
    }

    @Override
    public Task getTask(int type, int priority) {
        Task task = null;
        try {
            String key = ScheduleConstants.TOPIC + type + "_" + priority;
            String s = cacheService.lRightPop(key);
            if (StrUtil.isNotBlank(s)) {
                task = JSON.parseObject(s, Task.class);
                updateDb(task.getTaskId().toString(), ScheduleConstants.EXECUTED);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("拉取任务失败");
        }
        return task;

    }

    private void removeRedisTask(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority().toString();
        if (task.getExecuteTime() < System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.getstringRedisTemplate().opsForZSet().remove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    private Task updateDb(String taskId, int type) {
        Task task = null;
        try {
            taskinfoMapper.deleteById(taskId);
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(type);
            taskinfoLogsMapper.updateById(taskinfoLogs);
            task = BeanUtil.copyProperties(taskinfoLogs, Task.class);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.info("task cancel exe = {}", taskId);
            throw new RuntimeException();
        }
        return task;
    }

    private boolean addTaskToRedis(Task task) {
        try {
            //2.1 如果预设时间小于当前时间,存到list
            String key = task.getTaskType() + "_" + task.getPriority().toString();

            System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(task.getExecuteTime())));
            System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())));
            if (task.getExecuteTime() <= System.currentTimeMillis()) {
                cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
            }
            //2.2 如果小于当前时间加5分钟放入zset
            else if (task.getExecuteTime() <= System.currentTimeMillis() + 5 * 60 * 1000L) {
                cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private boolean addTaskToDb(Task task) {
        try {
            Taskinfo taskinfo = BeanUtil.copyProperties(task, Taskinfo.class);
            TaskinfoLogs taskinfoLogs = BeanUtil.copyProperties(task, TaskinfoLogs.class);

            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);
            task.setTaskId(taskinfo.getTaskId());
            taskinfoLogs.setTaskId(taskinfo.getTaskId());
            taskinfoLogs.setVersion(ScheduleConstants.INIT_VERSION);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * TODO:使用分布式定时任务：xxljob , quartz
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncTasksToWorkList() throws Exception{
        RLock lock = redissonClient.getLock(ScheduleConstants.SYNC_LOCK_NAME);
        boolean isLock = lock.tryLock(1000,1000*30, TimeUnit.MILLISECONDS);
        if(isLock){
            try {
                log.info("同步未来任务--->消费队列");
                Set<String> keys = cacheService.scan(ScheduleConstants.FUTURE + "*");
                //查找数据
                keys.forEach(item -> {
                    String topicKey = ScheduleConstants.TOPIC + item.split(ScheduleConstants.FUTURE)[1];
                    System.out.println(topicKey);
                    Set<String> tasks = cacheService.zRangeByScore(item, 0, System.currentTimeMillis());
                    //同步
                    if (!tasks.isEmpty()) {
                        cacheService.refreshWithPipeline(item, topicKey, tasks);
                        log.info("将" + item + "的任务刷新到了" + topicKey + "中数 据为:{}", tasks.toString());
                    }
                });
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 同步任务到redis
     */
    @Scheduled(cron = "0 */4 * * * ?")
    @PostConstruct
    public void reloadDbToRedis(){
        //清理缓存
        clearCacheTasks();
        //查找出db的task
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,5);
        List<Taskinfo> taskInfos = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery()
                                .le(Taskinfo::getExecuteTime,instance.getTime()));
        //添加
        taskInfos.forEach(item->{
            Task task = BeanUtil.copyProperties(item, Task.class);
            task.setExecuteTime(item.getExecuteTime().getTime());
            addTaskToRedis(task);
        });
    }
    private void clearCacheTasks(){
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
    }


}
