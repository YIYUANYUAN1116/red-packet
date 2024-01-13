package com.xht.red.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : YIYUANYUAN
 * @date: 2024/1/13  9:31
 * rabbit 延时交换机插件
 */
@Configuration
public class DelayPluginMQConfig {

    /**
    * @Author: yzd
    * @Date: 2024/1/13
     * 创建交换机
    */
    @Bean("redPacketExchange")
    public CustomExchange redPacketExchange(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange("redPacketExchange","x-delayed-message",true,false,arguments);
    }

    @Bean("redPacketQueue")
    public Queue redPacketQueue(){
        return QueueBuilder.durable("redPacketQueue").build();
    }

    @Bean
    public Binding redPacketBinding(){
        return BindingBuilder.bind(redPacketQueue()).to(redPacketExchange()).with("red.packet.key").noargs();
    }
}
