package com.heima.model.article.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author peelsannaw
 * @create 9/11/2022 下午5:50
 */
@ApiModel(value = "文章首页dto")
@Data
public class ArticleHomeDto {

    //首页分页查询需要传递时间,并且需要给时间设置数据库index
    @ApiModelProperty(value = "最大时间")
    private Date maxBehotTime;

    @ApiModelProperty(value = "最小时间")
    private Date minBehotTime;

    @ApiModelProperty(value = "频道ID")
    private String tag;

    @ApiModelProperty(value = "分页大小")
    Integer size = 10;


}
