package com.heima.wemedia.service;

import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @Author peelsannaw
 * @create 15/11/2022 下午10:44
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class WmNewAutoVerificationTest {

    @Resource
    private WmNewAutoVerification wmNewAutoVerification;

    @Test
    public void autoVerification() {
        wmNewAutoVerification.autoVerification(6232);
    }
}