package com.heima.common.constants.schedule;

public final class ScheduleConstants {
    //task状态
    public static final int SCHEDULED=0;   //初始化状态

    public static final int EXECUTED=1;       //已执行状态

    public static final int CANCELLED=2;   //已取消状态

    public static final Integer INIT_VERSION = 1;

    public static final String SYNC_LOCK_NAME = "FUTURE_TASKS_SYNC"; //分布式锁

    public static String FUTURE="future:";   //未来数据key前缀

    public static String TOPIC="topic:";     //当前数据key前缀
}
