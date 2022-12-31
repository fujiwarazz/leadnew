package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmLoginDto;
import com.heima.model.wemedia.dto.WmMaterialDto;
import com.heima.model.wemedia.entity.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    ResponseResult<?> getPicsList(WmMaterialDto wmMaterialDto);

//    /**
//     * 素材列表查询
//     * @param dto
//     * @return
//     */
//    public ResponseResult findList(WmMaterialDto dto);


}