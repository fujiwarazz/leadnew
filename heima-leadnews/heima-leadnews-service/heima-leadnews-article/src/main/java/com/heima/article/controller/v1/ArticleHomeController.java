package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.common.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午9:14
 */
@RestController
@RequestMapping("/api/v1/")
@Api(value = "app端口用户登录controller")
public class ArticleHomeController {

    @Resource
    private ApArticleService apArticleService;


    @PostMapping("/article/load")
    public ResponseResult<?> load(@RequestBody ArticleHomeDto dto){
        return apArticleService.load2(dto, ArticleConstants.LOAD_TYPE_LOAD_MORE,true);
    }

    @PostMapping("/article/loadmore")
    public ResponseResult<?> loadMore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOAD_TYPE_LOAD_MORE,dto);
    }

    @PostMapping("/article/loadnew")
    public ResponseResult<?> loadNew(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOAD_TYPE_LOAD_NEW,dto);
    }
    @PostMapping("/article/save")
    public ResponseResult<?> saveArticle(@RequestBody ArticleDto articleDto) {
        return apArticleService.saveArticleDTO(articleDto);
    }
    @PostMapping("/article/load_article_behavior")
    public ResponseResult<?> loadArticleBehavior(@RequestBody Map map){
        return apArticleService.loadArticleBehavior(map);
    }
    @PostMapping("/collection_behavior")
    public ResponseResult<?> userCollect(@RequestBody Map map){
        return apArticleService.userCollect(map);
    }
}