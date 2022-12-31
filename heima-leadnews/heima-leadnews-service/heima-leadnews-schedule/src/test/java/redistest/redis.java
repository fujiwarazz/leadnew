package redistest;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dto.Task;
import com.heima.model.schedule.entity.Taskinfo;
import com.heima.model.schedule.entity.TaskinfoLogs;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class redis {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    TaskService taskService;

    @Resource
    CacheService cacheService;

    @Resource
    TaskinfoLogsMapper mapper;

    @Test
    public void testredis(){
        List<TaskinfoLogs> taskinfoLogs = mapper.selectList(Wrappers.lambdaQuery());
        taskinfoLogs.forEach(item->{
            System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(item.getExecuteTime()));
        });

    }

    @Test
    public void testADD(){
        for(int i = 0;i<5;i++){
            Task task = new Task();
            task.setTaskType(100);
            task.setPriority(50);
            long time = System.currentTimeMillis()+5*50*1000;
            task.setExecuteTime(time);

            task.setParameters("task test7  ".getBytes());
            Long id = taskService.addTask(task);
        }

    }
    @Test
    public void getTask(){
    }


}
