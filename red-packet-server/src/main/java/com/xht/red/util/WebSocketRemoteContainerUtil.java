package com.xht.red.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.xht.red.constant.Constant;
import com.xht.red.model.RedPackgeVo;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * websocket工具栏
 * @author: yzd
 * @create: 2024-01-08
 **/
@Slf4j
public class WebSocketRemoteContainerUtil {

    //保存用户与Session关系
    // 注意：websocket的Session不能序列化到redis，只能在内存中保存，集群环境配合redis广播，通知所有用户
    private static Map<String, Session> tokenToSessionMap = new ConcurrentHashMap<>();

    public static void addSession(String token, Session session) {
        tokenToSessionMap.put(token, session);
    }

    public static void removeSession(String token) {
        tokenToSessionMap.remove(token);
    }

    public static Session getSession(String token) {
        return tokenToSessionMap.get(token);
    }

    public static void addTokenToActivity(String activityKey, String token, RedisTemplate redisTemplate) {
        redisTemplate.opsForSet().add(Constant.RED_PACKAGE_ACTIVITY_KEY+activityKey,token);
    }

    public static void removeTokenToActivity(String activityKey, String token, RedisTemplate redisTemplate) {
        redisTemplate.opsForSet().remove(Constant.RED_PACKAGE_ACTIVITY_KEY+activityKey,token);
    }

    public static Set<String> getActivityTokenList(String activityKey, RedisTemplate redisTemplate) {
       return (Set<String>) redisTemplate.opsForSet().members(Constant.RED_PACKAGE_ACTIVITY_KEY+activityKey);

    }

    /**
     * 单发消息
     * @author: yzd
     * @Date: 2024/1/8
     */
    public static void sendMsg(Session session, String token, RedPackgeVo redPackgeVo, RedisTemplate redisTemplate) {
        //异步发送消息.
        session.getAsyncRemote().sendText(JSON.toJSONString(redPackgeVo, SerializerFeature.DisableCircularReferenceDetect));
        //记录用户已经开启
        redisTemplate.opsForHash().put(Constant.RED_PACKAGE_USER_KEY+redPackgeVo.getRedPackageKey(),token,1);
        //设置过期时间
        redisTemplate.expire(Constant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityKey(), redPackgeVo.getDuration()+10000, TimeUnit.MILLISECONDS);
    }


    /**
     * 群发消息
     * @author: yzd
     * @Date: 2024/1/8
     */
    public static void sendMsg(RedPackgeVo redPackgeVo, RedisTemplate redisTemplate) {
        Set<String> activityTokenList = WebSocketRemoteContainerUtil.getActivityTokenList(redPackgeVo.getActivityKey(), redisTemplate);
        log.info(redPackgeVo.getActivityKey()+" 活动人数："+activityTokenList.size());
        if (!activityTokenList.isEmpty()){
            for (String token : activityTokenList) {
                Session session = WebSocketRemoteContainerUtil.getSession(token);
                if (null != session) {
                    session.getAsyncRemote().sendText(JSON.toJSONString(redPackgeVo, SerializerFeature.DisableCircularReferenceDetect));//异步发送消息.
                    //记录用户已开启
                    redisTemplate.opsForHash().put(Constant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityKey(), token, 1);
                    //设置过期时间
                    redisTemplate.expire(Constant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityKey(), redPackgeVo.getDuration()+10000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

}
