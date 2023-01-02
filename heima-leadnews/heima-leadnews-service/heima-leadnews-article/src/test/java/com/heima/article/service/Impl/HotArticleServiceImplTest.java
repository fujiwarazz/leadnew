package com.heima.article.service.Impl;

import com.heima.article.ArticleApplication;
import com.heima.article.service.HotArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @Author peelsannaw
 * @create 02/01/2023 20:00
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class HotArticleServiceImplTest {

    @Resource
    private HotArticleService hotArticleService ;

    @Test
    public void testHotArticle(){
        hotArticleService.computeHotArticleScore();
    }
}