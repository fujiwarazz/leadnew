package com.heima.model.admin.vos;

import com.heima.model.common.entity.WmSensitive;
import lombok.Data;

import java.util.List;

/**
 * @Author peelsannaw
 * @create 01/01/2023 16:15
 */
@Data
public class SensitiveWordsVo    {
    private Integer currentPage;
    private Long size;
    private Long total;
    private List<WmSensitive>data;
}
