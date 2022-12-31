package com.heima.freemarker.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午10:40
 */
@Data
public class Student {
    private String name;
    private int age;
    private Date birthday;
    private Float money;
}