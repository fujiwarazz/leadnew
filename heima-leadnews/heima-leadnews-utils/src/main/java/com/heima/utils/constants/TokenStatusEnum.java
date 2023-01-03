package com.heima.utils.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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


    @EnumValue
    public final Integer type;


    TokenStatusEnum(Integer type) {
        this.type = type;
    }


}

class father{
    int age = 40;
    void say(){
        System.out.println("i'am" + age+" years old");
    }
}
class son extends father{
    int age = 20;
    public static void main(String[] args) {
        father f = new son();

        //
    }
}

