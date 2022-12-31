package com.heima.wemedia.service;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @Author peelsannaw
 * @create 28/12/2022 20:29
 */
public interface WmTaskService {

    public void addNewsToTask(Integer id, Date pubDate);

    public void verifyTask();
}
