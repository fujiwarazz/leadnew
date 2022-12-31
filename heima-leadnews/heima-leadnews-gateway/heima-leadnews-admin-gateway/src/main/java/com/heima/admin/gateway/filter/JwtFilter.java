package com.heima.admin.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.heima.model.admin.entity.AdUser;
import com.heima.utils.common.AdminThreadLocalUtil;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.constants.TokenStatusEnum;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author peelsannaw
 * @create 01/01/2023 13:51
 */
@Component
public class JwtFilter implements Ordered, GlobalFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    private static final String TOKEN_PREFIX = "admin_login:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if(request.getURI().getPath().contains("/login")){
            //放行
            return chain.filter(exchange);
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否是过期
            TokenStatusEnum result = AppJwtUtil.verifyToken(claimsBody);
            String adminJson = stringRedisTemplate.opsForValue().get(JwtFilter.TOKEN_PREFIX + token);
            if(result.getType() == 1 || result.getType()  == 2){
                //如果过期的话判断redis内有没有过期,过期了的话在返回,如果没过期那么刷新token
                if(adminJson==null || adminJson.length()==0){
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }else{
                    refreshToken(adminJson);
                }
            }
            if(result.getType().equals(TokenStatusEnum.REFRESH_VALID.getType())){
                //刷新redis内的token
                refreshToken(adminJson);
            }
            assert claimsBody != null;

            Object userId = claimsBody.get("id");
            ServerHttpRequest serverHttpRequest = request.mutate().headers(header -> {
                header.add("userId", userId.toString());
            }).build();
            AdminThreadLocalUtil.setUser(JSON.parseObject(adminJson, AdUser.class));
            System.out.println(JSON.parseObject(adminJson, AdUser.class));
            //重置请求
            exchange.mutate().request(serverHttpRequest);
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6.放行
        return chain.filter(exchange);
    }
    public void refreshToken(String json){
        stringRedisTemplate.expire(json,15, TimeUnit.DAYS);
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
