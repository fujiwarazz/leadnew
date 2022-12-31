package com.heima.model.admin.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Author peelsannaw
 * @create 01/01/2023 16:10
 */
@Data
public class SensitiveWordsPageDto extends PageRequestDto {
    String name;
}
