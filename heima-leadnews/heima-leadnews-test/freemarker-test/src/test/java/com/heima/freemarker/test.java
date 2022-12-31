package com.heima.freemarker;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.heima.file.service.FileStorageService;
import com.heima.file.service.impl.MinIOFileStorageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.Data;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author peelsannaw
 * @create 10/11/2022 下午12:39
 */
@SpringBootTest(classes = FreemarkerDemotApplication.class)
@RunWith(SpringRunner.class)
public class test {
    @Resource
    private Configuration configuration;


    @Resource
    private FileStorageService fileStorageService;

    @Test
    public void testUpdateImgFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\courseDesign\\hmtt-workspace\\plugins\\css\\index.css");
            String filePath = fileStorageService.uploadHtmlFile("plugins/css", "index.css", fileInputStream);
            System.out.println(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test() throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient client = MinioClient.builder().credentials("minio", "minio123")
                .endpoint("http://192.168.200.130:9000").build();
        FileInputStream fileInputStream = new FileInputStream("D:\\courseDesign\\hmtt-workspace\\plugins\\js\\axios.min.js");
        PutObjectArgs leadnews = PutObjectArgs.builder()
                .object("plugins/js/axios.min.js")
                .contentType("text/js")
                .bucket("leadnews")
                .stream(fileInputStream,fileInputStream.available(),-1)
                .build();


        client.putObject(leadnews);

        System.out.println("http://192.168.200.130:9000/leadnews/list.html");
    }

    @Test
    public void test2() throws FileNotFoundException {
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String accessKeyId = "LTAI5tBkx5yHPJtJc2maaoj7";
        String accessKeySecret ="ah70DssneljMByNzla1oo0Gs2AqldB";
        String bucketName = "learning-hmtt-files";

        File file = new File("E://list.html");
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            // 创建OSS实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            //获取上传文件输入流
            //获取文件名称
            String fileName = file.getName();

//            //1 在文件名称里面添加随机唯一的值
//            String uuid = UUID.randomUUID().toString().replaceAll("-","");
//            // yuy76t5rew01.jpg
//            fileName = uuid+fileName;

            //2 把文件按照日期进行分类
//            //获取当前日期
//            String datePath = new DateTime().toString("yyyy/MM/dd");
//            //拼接
//            fileName = datePath+"/"+fileName;

            //调用oss方法实现上传
            //第一个参数  Bucket名称
            //第二个参数  上传到oss文件路径和文件名称   aa/bb/1.jpg
            //第三个参数  上传文件输入流
            ossClient.putObject(bucketName,fileName , fileInputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
            System.out.println(url);
        }catch(Exception e) {
            e.printStackTrace();

        }
    }
//
//    public static void main(String[] args) {
//      b b = new b();
//      b.setSex("male");
//      b b1 = new b();
//      b1.setSex("female");
//        System.out.println(Objects.equals(b,b1));
//
//    }
}
@Data
class A{
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof A)) return false;

        A a = (A) o;

        return name != null ? name.equals(a.name) : a.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
@Data
class b extends  A{
    String sex;
}
