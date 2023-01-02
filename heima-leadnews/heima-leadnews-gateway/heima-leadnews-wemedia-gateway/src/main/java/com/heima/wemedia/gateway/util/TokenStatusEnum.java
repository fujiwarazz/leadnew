package com.heima.wemedia.gateway.util;

import lombok.Getter;

/**
 * @Author peelsannaw
 * @create 8/11/2022 下午2:49
 */
@Getter
public enum TokenStatusEnum {

    /**
     * -1：有效， 0：刷新且有效， 1：过期， 2：过期
     */
    VALID(-1),
    REFRESH_VALID(0),
    EXPIRE(1),
    CHANGED(2);


    public final Integer type;


    TokenStatusEnum(Integer type) {
        this.type = type;
    }
}
