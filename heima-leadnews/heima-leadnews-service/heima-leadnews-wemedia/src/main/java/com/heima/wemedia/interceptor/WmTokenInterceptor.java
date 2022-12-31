package com.heima.wemedia.interceptor;

import com.heima.model.wemedia.entity.WmUser;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.WebUtil;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.function.ServerRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author peelsannaw
 * @create 11/11/2022 下午7:58
 */
@Component
public class WmTokenInterceptor implements HandlerInterceptor {

    @Resource
    WmUserMapper wmUserMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if(userId!=null){
            WmUser wmUser = wmUserMapper.selectById(userId);
            WmThreadLocalUtil.setUser(wmUser);
            return true;
        }
        else{
            WebUtil.renderString("请登录",401,response);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.clear();
    }
}
