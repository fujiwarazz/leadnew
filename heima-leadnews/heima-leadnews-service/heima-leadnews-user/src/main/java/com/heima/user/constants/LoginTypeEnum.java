package com.heima.user.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:50
 */
public enum LoginTypeEnum {

    PHONE("phone"),
    WECHAT("wechat"),
    QQ("qq"),
    ALI_PAY("ali_pay"),
    GUEST("guest");

    @EnumValue
    final String Name;

    LoginTypeEnum(String name) {
        Name = name;
    }
}
