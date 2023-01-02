package com.heima.user.interceptor;

import com.heima.model.apUser.entity.ApUser;
import com.heima.utils.common.ApUserThreadLocal;
import com.heima.utils.common.WebUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author peelsannaw
 * @create 31/12/2022 22:01
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if(userId!=null){
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            ApUserThreadLocal.setUser(apUser);
            return true;
        }
        else{
            WebUtil.renderString("请登录",401,response);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ApUserThreadLocal.clear();
    }
}
