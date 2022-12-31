package com.heima.schedule.service.Impl;

import com.heima.model.schedule.dto.Task;
import com.heima.schedule.service.TaskService;
import com.sun.jmx.snmp.tasks.TaskServer;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * @Author peelsannaw
 * @create 27/12/2022 20:48
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {

    @Resource
    private TaskService taskService;


    public void testAddTask(){
        Task t = new Task();
        t.setTaskType(100);
        t.setPriority(50);
        t.setParameters("test task".getBytes(StandardCharsets.UTF_8));
        t.setExecuteTime(System.currentTimeMillis()+500L);

    }


}