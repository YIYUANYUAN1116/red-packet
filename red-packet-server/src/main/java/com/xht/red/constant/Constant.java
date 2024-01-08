package com.xht.red.constant;

public class Constant {

    public static final String RED_PACKAGE_KEY = "red_package:";//拆分后的红包
    public static final String RED_PACKAGE_CONSUME_KEY = "red_package:consume:";
    public static final String RED_PACKAGE_INFO_KEY = "red_package:info:";//红包活动信息，开始才会有 RedPackgeVo
    public static final String RED_PACKAGE_LIST_KEY = "red_package:list";//创建红包活动信息 redPackgeDto
    public static final String RED_PACKAGE_USER_KEY = "red_package:user:";//参与活动的用户

    public static final String RED_PACKAGE_REDIS_QUEUE_KEY = "red_package:message:list";//消息list
    public static final String RED_PACKAGE_ACTIVITY_KEY = "red_package:activity";//保存本次活动用户token

}
