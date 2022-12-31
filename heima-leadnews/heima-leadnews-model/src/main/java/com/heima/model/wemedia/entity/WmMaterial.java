package com.heima.model.wemedia.entity;

/**
 * @Author peelsannaw
 * @create 11/11/2022 下午7:30
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 自媒体图文素材信息表
 * </p>
 *
 * @author itheima
 */
@Data
@TableName("wm_material")
public class WmMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 图片地址
     */
    @TableField("url")
    private String url;

    /**
     * 素材类型
     0 图片
     1 视频
     */
    @TableField("type")
    private Short type;

    /**
     * 是否收藏
     */
    private Short isCollection;

    /**
     * 创建时间
     */
    @TableField(value = "created_time",fill = FieldFill.INSERT_UPDATE)
    private Date createdTime;

}