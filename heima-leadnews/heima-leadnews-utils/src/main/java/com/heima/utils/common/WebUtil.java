package com.heima.utils.common;


import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * @Author peelsannaw
 * @create 9/11/2022 上午9:04
 */
public class WebUtil {
    public static void renderString(String msg, Integer status,HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(msg);

    }
}
