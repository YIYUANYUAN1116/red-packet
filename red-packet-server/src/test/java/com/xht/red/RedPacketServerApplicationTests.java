package com.xht.red;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //websocket
class RedPacketServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void getDate(){
        Date date = new Date();
        String jsonString = JSON.toJSONString(date);
        System.out.println(jsonString);
    }
}
