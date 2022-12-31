package com.heima.model.schedule.dto;

import com.heima.model.schedule.entity.Taskinfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author peelsannaw
 * @create 19/12/2022 上午12:03
 */
@Data
public class Task implements Serializable {


    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行id
     */
    private long executeTime;

    /**
     * task参数
     */
    private byte[] parameters;

}