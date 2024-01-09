package com.xht.red.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.xht.red.common.Result;
import com.xht.red.common.ResultCodeEnum;
import com.xht.red.constant.Constant;
import com.xht.red.model.RedPackgeDto;
import com.xht.red.util.RedPackageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: yzd
 * @create: 2024-01-09
 **/

@RequestMapping("/api/v1")
@Tag(name = "红包活动接口V1")
@Slf4j
public class RedPackageV1Controller {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发红包
     * @author: yzd
     * @Date: 2024/1/9
     */

    @PostMapping("/add")
    @Operation(summary = "发红包")
    @GetMapping(value = "/send/{totalMoney}/{redPackageNumber}")
    public Result<String> sendRedPackage(@PathVariable int totalMoney, @PathVariable int redPackageNumber) {
        //拆分红包
        Integer[] splitRedPackages = RedPackageUtil.splitRedPackageAlgorithm(totalMoney, redPackageNumber);
        log.info("拆红包: {}", JSON.toJSONString(splitRedPackages));

        String key = IdUtil.simpleUUID();
        redisTemplate.opsForList().leftPushAll(Constant.RED_PACKAGE_KEY + key, splitRedPackages);
        redisTemplate.expire(Constant.RED_PACKAGE_KEY + key, 1, TimeUnit.DAYS);


        return Result.build(key, ResultCodeEnum.SUCCESS);
    }
    /**
     * 抢红包
     * @author: yzd
     * @Date: 2024/1/9
     */

    @Operation(summary = "抢红包")
    @GetMapping(value = "/rob1/{redPackageKey}")
    public Result robRedPackage1( @PathVariable String redPackageKey, @RequestHeader("token") String token) {

        //1 验证某个用户是否抢过红包，不可以多抢
        Object redPackage = redisTemplate.opsForHash().get(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey, token);

        if (redPackage == null){

            Object partRedPackage = redisTemplate.opsForList().leftPop(Constant.RED_PACKAGE_KEY + redPackageKey);
            if(partRedPackage != null){
                //2.2 抢到红包后需要记录进入hash结构，表示谁抢到了多少钱的某个子红包
                redisTemplate.opsForHash().put(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey, token, partRedPackage);
                log.info("用户:{} 抢到了多少钱的红包：{}", token, partRedPackage);
                //TODO 后续异步进mysql或者MQ进一步做统计处理,每一年你发出多少红包，抢到了多少红包，年度总结
                return Result.build(partRedPackage, ResultCodeEnum.SUCCESS);
            }
        }

        return Result.build(null, ResultCodeEnum.RED_PACKAGE_REAPT);
    }
    /**
     * 查看记录
     * @author: yzd
     * @Date: 2024/1/9
     */

    @Operation(summary = "领取记录")
    @GetMapping(value = "/record1/{redPackageKey}")
    public Result<Object> redPackageRecord1( @PathVariable String redPackageKey,
                                            @RequestHeader("token") String token) {
        Map<String, Integer> map = redisTemplate.opsForHash().entries(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey);
        //当前用户的领取金额
        //return Result.build(map.get(token), ResultCodeEnum.SUCCESS);
        //全部用户的领取金额
        return Result.build(map, ResultCodeEnum.SUCCESS);
    }
}
