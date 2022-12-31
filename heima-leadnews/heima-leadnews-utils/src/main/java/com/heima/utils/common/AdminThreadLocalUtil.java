package com.heima.utils.common;

import com.heima.model.admin.entity.AdUser;
import com.heima.model.wemedia.entity.WmUser;

/**
 * @Author peelsannaw
 * @create 01/01/2023 14:16
 */
public class AdminThreadLocalUtil {
    private final static ThreadLocal<AdUser> AD_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     * @param wmUser
     */
    public static void  setUser(AdUser wmUser){
        AD_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取用户
     */
    public static AdUser getUser(){
        return AD_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear(){
        AD_USER_THREAD_LOCAL.remove();
    }
}
