package com.xht.red.message;

/**
 * @author: yzd
 * @create: 2024-01-08
 **/

import com.xht.red.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * redis广播通知配置类：订阅发布
 */
@Configuration
public class RedisChannelConfig {


    /**
     * Redis消息监听器容器
     * 可以添加一个或多个监听不同主题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定
     * 通过反射技术调用消息订阅处理器的相关方法进行业务处理
     * @author: yzd
     * @Date: 2024/1/8
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅主题， 这个container 可以添加多个 messageListener
        container.addMessageListener(listenerAdapter, new PatternTopic(Constant.RED_PACKAGE_REDIS_QUEUE_KEY));
        return container;
    }

    /**
     * @author: yzd
     * @Date: 2024/1/8
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMsgListener redisMsgListener){
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器
        return new MessageListenerAdapter(redisMsgListener);
    }
}
