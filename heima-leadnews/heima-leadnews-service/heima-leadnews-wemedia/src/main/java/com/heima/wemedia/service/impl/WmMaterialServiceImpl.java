package com.heima.wemedia.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.wm.WmConstants;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;

import com.heima.model.wemedia.dto.WmMaterialDto;
import com.heima.model.wemedia.entity.WmMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


/**
 * @author peelsannaw
 */
@Slf4j
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Resource
    FileStorageService fileStorageService;

    @Override
    public ResponseResult<?> uploadPicture(MultipartFile multipartFile) {
        if( multipartFile==null || multipartFile.getSize()==0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String originalName = multipartFile.getOriginalFilename();
        assert originalName != null;
        WmMaterial wmMaterial = null;
        try {
            String fileName = RandomUtil.randomString(16).replace("-","")
                    +originalName.substring(originalName.lastIndexOf("."));
            String url = fileStorageService.uploadImgFile("", fileName, multipartFile.getInputStream());
            wmMaterial = new WmMaterial();
            wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
            wmMaterial.setUrl(url);
            wmMaterial.setIsCollection((short)0);
            wmMaterial.setType((short)0);
            this.save(wmMaterial);
            return ResponseResult.okResult(wmMaterial);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }
    }



    @Override
    @SuppressWarnings("unchecked")
    public ResponseResult<?> getPicsList(WmMaterialDto materialDto) {
        materialDto.checkParam();
        LambdaQueryWrapper<WmMaterial>queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId());
        if(materialDto.getIsCollection()!=null ){
            queryWrapper.eq(WmMaterial::getIsCollection,materialDto.getIsCollection());
        }
        queryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        Page<WmMaterial> page = this.page(new Page<>(materialDto.getPage(), materialDto.getSize()), queryWrapper);
        PageResponseResult pageResponseResult = new PageResponseResult(materialDto.getPage(), materialDto.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }
}

