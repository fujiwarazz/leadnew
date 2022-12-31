package com.heima.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.apUser.dto.LoginDto;
import com.heima.model.apUser.entity.ApUser;
import com.heima.model.apUser.vo.LoginUserVo;
import com.heima.model.apUser.vo.LoginVo;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:57
 */
@Service
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper,ApUser> implements ApUserService{

    @Resource
    private ApUserMapper apUserMapper;

    @Override
    public LoginVo authLogin(LoginDto loginDto) {

        String phone = loginDto.getPhone();
        String password = loginDto.getPassword();
        //正常登录
        if(StrUtil.isNotBlank(phone) && StrUtil.isNotBlank(password)){
            ApUser dbUser = lambdaQuery().eq(ApUser::getPhone, phone).one();
            if(dbUser==null){
                throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
            }
            String salt = dbUser.getSalt();
            String loginDtoPassword = loginDto.getPassword();
            String saltPassword = DigestUtils.md5DigestAsHex((loginDtoPassword + salt).getBytes());
            if(!saltPassword.equals(dbUser.getPassword())){
                throw new CustomException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            LoginUserVo loginUserVo = BeanUtil.copyProperties(dbUser, LoginUserVo.class);
            return new LoginVo(loginUserVo,token,false);
        }else{
            //游客登录
            String token = AppJwtUtil.getToken(0L);
            return new LoginVo(null,token,true);
        }


    }
}
