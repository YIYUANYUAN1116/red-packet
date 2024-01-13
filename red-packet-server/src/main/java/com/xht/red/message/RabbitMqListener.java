package com.xht.red.message;

import com.rabbitmq.client.Channel;
import com.xht.red.model.RedPackgeVo;
import com.xht.red.util.WebSocketRemoteContainerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : YIYUANYUAN
 * @date: 2024/1/13  9:43
 */

@Component
@Slf4j
public class RabbitMqListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = {"redPacketQueue"})
    public void redPacketListener(RedPackgeVo redPackgeVo, Message message, Channel channel){
        log.info("收到消息"+redPackgeVo.toString());
        try {
            //WebSocket发送消息
            WebSocketRemoteContainerUtil.sendMsg(redPackgeVo, redisTemplate);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
