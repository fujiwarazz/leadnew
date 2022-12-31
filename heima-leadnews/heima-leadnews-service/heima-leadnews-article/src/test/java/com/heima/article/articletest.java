package com.heima.article;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 11/11/2022 下午3:33
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class articletest {

    @Resource
    Configuration configuration;
    @Resource
    ApArticleContentMapper apArticleContentMapper;
    @Resource
    FileStorageService fileStorageService;
    @Resource
    ApArticleService apArticleService;

    @Test
    public void createStaticURL() throws IOException, TemplateException {

        ApArticleContent apArticleContent = apArticleContentMapper
                .selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                        .eq(ApArticleContent::getArticleId, 1302862387124125698L));
        Template template = configuration.getTemplate("article.ftl");
        if(apArticleContent!=null && StrUtil.isNotBlank(apArticleContent.getContent())){
            Map<String,Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            StringWriter stringWriter = new StringWriter();
            template.process(map,stringWriter);
            InputStream in = new ByteArrayInputStream(stringWriter.toString().getBytes());
            String s = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

            boolean update = apArticleService.update(Wrappers
                    .<ApArticle>lambdaUpdate()
                    .eq(ApArticle::getId, 1302862387124125698L)
                    .set(ApArticle::getStaticUrl, s));

            System.out.println(update);


        }


    }

}
