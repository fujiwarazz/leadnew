package com.heima.wemedia.runner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.common.entity.WmSensitive;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author peelsannaw
 * @create 16/12/2022 下午7:53
 */
@Component
public class SensitiveInit implements CommandLineRunner {

    @Resource
    private WmSensitiveMapper wmSensitiveMapper;
    @Override
    public void run(String... args) throws Exception {
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> collect = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(collect);
    }
}
