package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.SensitiveWordsInsertDto;
import com.heima.model.admin.dtos.SensitiveWordsPageDto;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.wemedia.dto.WmNewsDto;
import com.heima.model.wemedia.dto.WmNewsPageReqDto;
import com.heima.model.wemedia.dto.WmNewsUpdateDto;
import com.heima.model.wemedia.entity.WmNews;

/**
 * @author peelsannaw
 */
public interface WmNewsService extends IService<WmNews> {


    ResponseResult<?> findAll(WmNewsPageReqDto dto);

    ResponseResult<?> submitNews(WmNewsDto wmNewsDto);

    ResponseResult<?> downOrUp(WmNewsUpdateDto wmnewsUpdateDto);

    ResponseResult<?> deleteSensitiveWord(Integer id);

    ResponseResult<?> getList(SensitiveWordsPageDto pageDto);

    ResponseResult<?> saveSensitiveWord(SensitiveWordsInsertDto dto);
}
