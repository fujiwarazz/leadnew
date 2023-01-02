package com.heima.apis.wmnews;

import com.heima.model.admin.dtos.SensitiveWordsInsertDto;
import com.heima.model.admin.dtos.SensitiveWordsPageDto;
import com.heima.common.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author peelsannaw
 * @create 01/01/2023 15:47
 */
@FeignClient(value = "leadnews-wemedia")
public interface IWmSensitiveClient {

    @DeleteMapping("/api/v1/sensitive/del/{id}")
    ResponseResult<?> deleteWordById(@PathVariable Integer id);

    @PostMapping("/api/v1/sensitive/list")
    ResponseResult<?> getListByDto(@RequestBody SensitiveWordsPageDto pageDto);

    @PostMapping("/api/v1/sensitive/save")
    ResponseResult<?> saveSensitiveWords(@RequestBody SensitiveWordsInsertDto dto);
}
