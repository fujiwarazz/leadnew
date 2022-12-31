package com.heima.article.controller.v1;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午9:14
 */
@RestController
@RequestMapping("/api/v1/article")
@Api(value = "app端口用户登录controller")
public class ArticleHomeController {

    @Resource
    private ApArticleService apArticleService;


    @PostMapping("/load")
    public ResponseResult<?> load(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOAD_TYPE_LOAD_MORE,dto);
    }

    @PostMapping("/loadmore")
    public ResponseResult<?> loadMore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOAD_TYPE_LOAD_MORE,dto);
    }

    @PostMapping("/loadnew")
    public ResponseResult<?> loadNew(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOAD_TYPE_LOAD_NEW,dto);
    }
    @PostMapping("save")
    public ResponseResult<?> saveArticle(@RequestBody ArticleDto articleDto) {
        return apArticleService.saveArticleDTO(articleDto);
    }
}