package com.heima.model.wemedia.dto;

import lombok.Data;

/**
 * @Author peelsannaw
 * @create 30/12/2022 10:07
 */
@Data
public class WmNewsUpdateDto {

    /**
     * wmNews 主键
     */
    private Integer id;
    /**
     * 是否上架  0 下架  1 上架
     */
    private Short enable;
}
