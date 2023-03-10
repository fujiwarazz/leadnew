package com.heima.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.apUser.dto.LoginDto;
import com.heima.model.apUser.entity.ApUser;
import com.heima.model.apUser.vo.LoginUserVo;
import com.heima.model.apUser.vo.LoginVo;
import com.heima.model.user.dto.UserFollowDto;
import com.heima.user.constants.UserConstants;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.ApUserThreadLocal;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author peelsannaw
 * @create 7/11/2022 下午10:57
 */
@Service
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper,ApUser> implements ApUserService{

    @Resource
    private ApUserMapper apUserMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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

    @Override
    public ResponseResult<?> userFollow(UserFollowDto dto) {
        if(dto.getAuthorId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        try {
            System.out.println(ApUserThreadLocal.getUser().getId());
            String key = UserConstants.USER_FOLLOW_PREFIX + ApUserThreadLocal.getUser().getId();
            Boolean member = stringRedisTemplate.opsForSet().isMember(key, dto.getAuthorId().toString());
            if(Boolean.FALSE.equals(member)){
                stringRedisTemplate.opsForSet().add(key,dto.getAuthorId().toString());
            }else{
                stringRedisTemplate.opsForSet().remove(key,dto.getAuthorId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();

    }

    private <T> boolean checkParams(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T t = clazz.newInstance();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if(isGetter(method)){
                Object invoke = method.invoke(t);
                if(invoke == null){
                    return false;
                }
            }
        }
        return true;
    }
    private static boolean isGetter(Method method){
        if(!method.getName().startsWith("get")) {
            return false;
        }
        //get方法肯定没有参数
        if(method.getParameterTypes().length != 0) {
            return false;
        }
        if(void.class.equals(method.getReturnType())){
            return false;
        }
        return true;
    }

}
