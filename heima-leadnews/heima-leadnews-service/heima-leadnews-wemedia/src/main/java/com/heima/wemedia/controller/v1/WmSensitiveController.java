package com.heima.wemedia.controller.v1;

import com.heima.apis.wmnews.IWmSensitiveClient;
import com.heima.model.admin.dtos.SensitiveWordsInsertDto;
import com.heima.model.admin.dtos.SensitiveWordsPageDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 01/01/2023 17:23
 */
@RestController
public class WmSensitiveController implements IWmSensitiveClient {
    @Resource
    private WmNewsService wmNewsService;

    @Override
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    public ResponseResult<?> deleteWordById(@PathVariable Integer id) {
        return wmNewsService.deleteSensitiveWord(id);
    }

    @Override
    @PostMapping("/api/v1/sensitive/list")
    public ResponseResult<?> getListByDto(@RequestBody SensitiveWordsPageDto pageDto) {
        return wmNewsService.getList(pageDto);
    }

    @Override
    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult<?> saveSensitiveWords(SensitiveWordsInsertDto dto) {
        return wmNewsService.saveSensitiveWord(dto);
    }
}
