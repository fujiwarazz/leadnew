package com.heima.admin.controller.v1;

import com.heima.apis.wmnews.IWmSensitiveClient;
import com.heima.model.admin.dtos.SensitiveWordsInsertDto;
import com.heima.model.admin.dtos.SensitiveWordsPageDto;
import com.heima.common.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 01/01/2023 15:42
 */
@RestController
@RequestMapping("/api/v1/sensitive/")
public class SensitiveWordController {

    @Resource
    IWmSensitiveClient iWmnewsClient;

    @DeleteMapping("/del/{id}")
    public ResponseResult<?> deleteSensitiveWord(@PathVariable Integer id){
        return iWmnewsClient.deleteWordById(id);
    }

    @PostMapping("/list")
    public ResponseResult<?> getSensitiveWords(@RequestBody SensitiveWordsPageDto pageDto){
        return iWmnewsClient.getListByDto(pageDto);
    }

    @PostMapping("save")
    public ResponseResult<?> saveSensitiveWords(@RequestBody SensitiveWordsInsertDto dto){
        return iWmnewsClient.saveSensitiveWords(dto);
    }
}
