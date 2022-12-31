import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 13/11/2022 上午9:44
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class test {

    @Resource
    GreenImageScan greenImageScan;
    @Resource
    GreenTextScan greenTextScan;
    @Resource
    FileStorageService fileStorageService;
    @Test
    public void testTxtScan() throws Exception {
        Map map = greenTextScan.greeTextScan("我日你妈");
        System.out.println(map);
    }
    @Test
    public void testImageScan() throws Exception{
        byte[] bytes = fileStorageService.downLoadFile("http://192.168.200.130:9000/leadnews/2022/11/12/jcydvwribathjjuo.jpg");
        List<byte[]>list = new ArrayList<>();
        list.add(bytes);
        Map map = greenImageScan.imageScan(list);
        System.out.println(map);
    }


}

