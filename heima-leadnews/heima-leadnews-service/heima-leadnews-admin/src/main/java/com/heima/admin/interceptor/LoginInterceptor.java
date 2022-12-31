package com.heima.admin.interceptor;

import com.heima.admin.mapper.AdUserMapper;
import com.heima.model.admin.entity.AdUser;
import com.heima.utils.common.AdminThreadLocalUtil;
import org.aopalliance.intercept.Interceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:18
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private AdUserMapper adUserMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getHeader("userId")!=null && request.getHeader("userId").trim().length()>0){
            AdUser userId = adUserMapper.selectById(request.getHeader("userId"));
            AdminThreadLocalUtil.clear();
            AdminThreadLocalUtil.setUser(userId);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        AdminThreadLocalUtil.clear();
    }
}
