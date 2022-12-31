import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.apUser.entity.ApUser;
import com.heima.user.UserApplication;
import com.heima.user.service.ApUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 11/11/2022 下午4:20
 */
@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
public class a {

    @Resource
    ApUserService apUserService;
    @Test
    public void test(){
        ApUser apUser = apUserService.getById("5");
        apUserService.update(Wrappers.lambdaUpdate(apUser).set(ApUser::getSalt,"1233"));
        System.out.println(apUser.getSalt());
    }
}
