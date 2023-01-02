package com.heima.model.wemedia.dto;

import com.heima.common.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

/**
 * @Author peelsannaw
 * @create 12/11/2022 下午8:34
 */
@Data

public class WmNewsPageReqDto extends PageRequestDto {
    /**
     * 状态
     */
    private Short status;
    /**
     * 开始时间
     */
    private Date beginPubDate;
    /**
     * 结束时间
     */
    private Date endPubDate;
    /**
     * 所属频道ID
     */
    private Integer channelId;
    /**
     * 关键字
     */
    private String keyword;
}
