package com.heima.utils.common;

import com.heima.model.user.entity.ApUser;
import com.heima.model.wemedia.entity.WmUser;

/**
 * @Author peelsannaw
 * @create 31/12/2022 22:05
 */
public class ApUserThreadLocal {
    private final static ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     * @param apUser
     */
    public static void  setUser(ApUser apUser){
        AP_USER_THREAD_LOCAL.set(apUser);
    }

    /**
     * 获取用户
     */
    public static ApUser getUser(){
        return AP_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear(){
        AP_USER_THREAD_LOCAL.remove();
    }
}
