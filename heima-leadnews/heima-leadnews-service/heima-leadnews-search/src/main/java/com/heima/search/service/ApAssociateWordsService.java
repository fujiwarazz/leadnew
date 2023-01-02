package com.heima.search.service;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

/**
 * @Author peelsannaw
 * @create 31/12/2022 23:25
 */
public interface ApAssociateWordsService {
    /**
     联想词
     @param userSearchDto
     @return
     */
    ResponseResult findAssociate(UserSearchDto userSearchDto);
}
