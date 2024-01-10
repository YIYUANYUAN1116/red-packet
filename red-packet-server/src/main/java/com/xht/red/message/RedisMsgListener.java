package com.xht.red.message;

import com.alibaba.fastjson2.JSON;
import com.xht.red.model.RedPackgeVo;
import com.xht.red.util.WebSocketRemoteContainerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: yzd
 * @create: 2024-01-08
 **/

@Component
public class RedisMsgListener implements MessageListener {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object deserialize = redisTemplate.getValueSerializer().deserialize(new String(message.getBody()).getBytes());
        if (deserialize != null){
            RedPackgeVo redPackgeVo = JSON.parseObject(deserialize.toString(),RedPackgeVo.class);
            //WebSocket发送消息
            WebSocketRemoteContainerUtil.sendMsg(redPackgeVo, redisTemplate);
        }
    }
}
