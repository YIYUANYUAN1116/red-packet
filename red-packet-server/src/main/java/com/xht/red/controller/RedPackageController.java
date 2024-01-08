package com.xht.red.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.xht.red.common.Result;
import com.xht.red.common.ResultCodeEnum;
import com.xht.red.constant.Constant;
import com.xht.red.model.RedPackgeDto;
import com.xht.red.model.RedPackgeVo;
import com.xht.red.util.RedPackageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: yzd
 * @create: 2024-01-08
 **/

@RequestMapping("/api")
@Tag(name = "红包活动接口")
@Slf4j
public class RedPackageController {


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加活动
     * @author: yzd
     * @Date: 2024/1/8
     */
    @PostMapping("/add")
    public Result add(@RequestBody RedPackgeDto redPackgeDto){
        //1.记录活动信息
        redPackgeDto.setActivityKey("hd_"+ IdUtil.simpleUUID());
        redisTemplate.opsForSet().add(Constant.RED_PACKAGE_LIST_KEY,redPackgeDto);

        // 2.活动开始后才初始化红包雨相关信息，保证所有用户同一时刻抢红包（公平、准点、公正）
        // 2.1 计算活动开始的剩余时间：单位秒(方法用于将此瞬间与时区结合在一起)
        LocalDateTime localDateTime = redPackgeDto.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long delayTime = ChronoUnit.SECONDS.between(LocalDateTime.now(), localDateTime);

        //todo 2.2 启动定时任务，注：正式环境可改为rabbitmq/rocketmq延迟消息，当前只是模拟
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            //2.2.1活动开始，拆分红包
            Integer[] splitRedPackages = RedPackageUtil.splitRedPackageAlgorithm(redPackgeDto.getTotalMoney(), redPackgeDto.getRedPackageNumber());
            log.info("拆红包: {}", JSON.toJSONString(splitRedPackages));
            String uuid = IdUtil.simpleUUID();
            redisTemplate.opsForList().leftPushAll(Constant.RED_PACKAGE_KEY+uuid,splitRedPackages);

            // 2.2.3构建前端红包雨活动数据
            RedPackgeVo redPackgeVo = new RedPackgeVo();
            redPackgeVo.setGenerationRate(redPackgeDto.getGenerationRate());
            redPackgeVo.setDuration(redPackgeDto.getDuration());
            redPackgeVo.setActivityKey(redPackgeDto.getActivityKey());
            redPackgeVo.setRedPackageKey(uuid);

            //保存红包雨活动数据，后续会使用
            redisTemplate.opsForValue().set(Constant.RED_PACKAGE_INFO_KEY+uuid,redPackgeVo,redPackgeDto.getDuration()+10000, TimeUnit.MILLISECONDS);

            // 2.2.4redis广播信息，服务器收到广播消息后，websocket推送消息给前端用户开启红包雨活动
            redisTemplate.convertAndSend(Constant.RED_PACKAGE_REDIS_QUEUE_KEY, JSON.toJSONString(redPackgeVo));
            log.info("红包雨活动广播：{}", JSON.toJSONString(redPackgeVo));

        },delayTime, TimeUnit.SECONDS);

        executor.shutdown();
        return Result.build(redPackgeDto.getActivityKey(), ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "活动列表")
    @GetMapping(value = "/list")
    public Result<List<RedPackgeDto>> listRedPackage() {
        Set<RedPackgeDto> redPackgeDtoSet = redisTemplate.opsForSet().members(Constant.RED_PACKAGE_LIST_KEY);
        List<RedPackgeDto> redPackgeDtos = null;

        if (redPackgeDtoSet != null){
            redPackgeDtos = redPackgeDtoSet.stream()
                    .sorted(Comparator.comparing(RedPackgeDto::getDate)).toList();
        }

        return Result.build(redPackgeDtos,ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "抢红包")
    @GetMapping(value = "/rob/{redPackageKey}")
    public Result robRedPackage2(@PathVariable String redPackageKey, @RequestHeader("token") String token) {
        //1 不限制用户抢红包个数。谁快谁抢的多
        Object redPackage = redisTemplate.opsForList().rightPop(Constant.RED_PACKAGE_KEY + redPackageKey);
        if (redPackage != null){
            //抢到红包，保存起来来
            redisTemplate.opsForList().leftPush(Constant.RED_PACKAGE_CONSUME_KEY+redPackageKey + ":" + token,redPackage);
            //设置过期时间，相当活动时间长一点即可
            redisTemplate.expire(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey + ":" + token, 1, TimeUnit.HOURS);
            log.info("用户:{} 抢到了多少钱的红包：{}", token, redPackage);
            //TODO 后续异步进mysql或者MQ进一步做统计处理,每一年你发出多少红包，抢到了多少红包，年度总结
            return Result.build(redPackage, ResultCodeEnum.SUCCESS);
        }

        log.info("红包池已抢空，红包标识：{}", redPackageKey);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "领取记录")
    @GetMapping(value = "/record/{redPackageKey}")
    public Result<Integer> redPackageRecord2( @PathVariable String redPackageKey, @RequestHeader("token") String token) {
        Long size = redisTemplate.opsForList().size(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey + ":" + token);
        List<Integer> range = redisTemplate.opsForList().range(Constant.RED_PACKAGE_CONSUME_KEY + redPackageKey + ":" + token, 0, size - 1);
        Object total = range.stream().reduce(0, Integer::sum);
        return Result.build(total, ResultCodeEnum.SUCCESS);
    }
}

