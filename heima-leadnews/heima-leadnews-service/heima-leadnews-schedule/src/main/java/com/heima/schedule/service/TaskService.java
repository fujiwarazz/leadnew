package com.heima.schedule.service;

import com.heima.model.schedule.dto.Task;

public interface TaskService {
    /**
     * 添加任务
     * @param task   任务对象
     * @return       任务id
     */
    public Long addTask(Task task) ;

    public boolean cancel(String taskId );

    public Task getTask(int type,int priority);
}
