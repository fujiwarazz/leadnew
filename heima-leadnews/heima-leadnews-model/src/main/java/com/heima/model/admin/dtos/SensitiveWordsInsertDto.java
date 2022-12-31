package com.heima.model.admin.dtos;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @Author peelsannaw
 * @create 01/01/2023 16:25
 */
@Data
public class SensitiveWordsInsertDto {
  private Integer id;
  private String sensitives;
  private String createdTime;
}
