package com.xht.red.controller;

import com.xht.red.constant.Constant;
import com.xht.red.model.RedPackgeVo;
import com.xht.red.util.WebSocketRemoteContainerUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * * WebSocket接口测试工具：http://www.jsons.cn/websocket/
 * * 接口地址：ws://ip:port/api/websocket/{activityKey}/{token}
 *
 * @author: yzd
 * @create: 2024-01-08
 **/
@Slf4j
@Tag(name = "即时通讯接口管理")
@ServerEndpoint(value = "/api/websocket/{activityKey}/{token}")
@Component
public class WebSocketApiController {
    //运行时的 WebSocket 连接对象，也就是端点实例，是由服务器创建，而不是 Spring，所以不能使用自动装配。
    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        WebSocketApiController.redisTemplate = redisTemplate;
    }

    /**
     * 连接成功调用
     * @author: yzd
     * @Date: 2024/1/8
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("activityKey") String activityKey, @PathParam("token") String token){
        log.info("连接成功，活动：{}，用户：{}，sessionId:{}", activityKey, token,session.getId());
        //添加token 与 session 的活动关联
        WebSocketRemoteContainerUtil.addSession(token,session);
        //添加活动与token的关联
        WebSocketRemoteContainerUtil.addTokenToActivity(activityKey,token,redisTemplate);

        //活动开始后，新进用户直接通知活动
        RedPackgeVo redPackgeVo = (RedPackgeVo) redisTemplate.opsForValue().get(Constant.RED_PACKAGE_INFO_KEY + activityKey);
        if (redPackgeVo != null){
            //活动开始后，同一个用户不能重复开启红包雨
            Object object = redisTemplate.opsForHash().get(Constant.RED_PACKAGE_USER_KEY + activityKey, token);
            if (object == null){
                WebSocketRemoteContainerUtil.sendMsg(session,token,redPackgeVo,redisTemplate);
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("activityKey") String activityKey, @PathParam("token") String token,String message){
//        log.info("[websocket] 收到消息：sessionId={}，message={}", session.getId(), message);

    }


        /**
         * @author: yzd
         * @Date: 2024/1/8
         */
    @OnClose
    public void onClose(Session session, @PathParam("activityKey") String activityKey, @PathParam("token") String token){
        log.info("退出连接，活动：{}，用户：{}", activityKey, token);

        WebSocketRemoteContainerUtil.removeSession(token);
        WebSocketRemoteContainerUtil.removeTokenToActivity(activityKey, token, redisTemplate);
    }

    /**
     * @author: yzd
     * @Date: 2024/1/8
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

}
