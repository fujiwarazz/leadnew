package com.heima.search.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.search.service.ApUserSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 31/12/2022 23:05
 */
@RestController
@RequestMapping("/api/v1/history")
public class ApArticleHistoryController {

    @Resource
    private ApUserSearchService apUserSearchService;
    @PostMapping("/load")
    public ResponseResult<?> getUserSearchHistory(){
        return apUserSearchService.getUserSearchHistory();
    }

    @PostMapping("/del")
    public ResponseResult<?> delUserSearchHistory(@RequestBody Map request){
        return apUserSearchService.delUserSearchHistory(request);
    }
}
