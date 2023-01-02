package com.heima.wemedia.controller.v1;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dto.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 11/11/2022 下午8:10
 */
@RequestMapping("/api/v1/material")
@RestController
public class WmMaterialController {

    @Resource
    WmMaterialService materialService;

    @PostMapping("/upload_picture")
    public ResponseResult<?> uploadPic(MultipartFile multipartFile){
        return materialService.uploadPicture(multipartFile);
    }
    @PostMapping("list")
    public ResponseResult<?> getPicsList(@RequestBody WmMaterialDto wmMaterialDto){
         return materialService.getPicsList(wmMaterialDto);
    }
    @GetMapping("/del_picture/{id}")
    public ResponseResult<?>deletePic(@PathVariable String id){
        boolean flag = materialService.removeById(id);
        if(flag){
            return ResponseResult.okResult();
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }
}
