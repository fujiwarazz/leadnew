package com.heima.search.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.search.service.ArticleSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author peelsannaw
 * @create 30/12/2022 23:13
 */
@RestController
@RequestMapping("/api/v1/article/search")
public class ArticleSearchController {

    @Resource
    private ArticleSearchService articleSearchService;

    @PostMapping("search")
    public ResponseResult<?> search(@RequestBody UserSearchDto userSearchDto) throws IOException {
        return articleSearchService.search(userSearchDto);
    }
}
