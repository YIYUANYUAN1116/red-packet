package com.xht.red.message;

import com.alibaba.fastjson2.JSON;
import com.xht.red.model.RedPackgeVo;
import com.xht.red.util.WebSocketRemoteContainerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: yzd
 * @create: 2024-01-08
 **/

@Component
public class RedisMsgListener {

    @Autowired
    RedisTemplate redisTemplate;

    public void receiveMessage(String message){
        Object deserialize = redisTemplate.getValueSerializer().deserialize(message.getBytes());
        if (deserialize != null){
            RedPackgeVo redPackgeVo = JSON.parseObject(deserialize.toString(),RedPackgeVo.class);
            //WebSocket发送消息
            WebSocketRemoteContainerUtil.sendMsg(redPackgeVo, redisTemplate);
        }
    }
}
