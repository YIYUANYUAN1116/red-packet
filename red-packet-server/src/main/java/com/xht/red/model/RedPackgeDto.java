package com.xht.red.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "红包雨活动信息类")
public class RedPackgeDto {

    @Schema(description = "红包雨活持续时长:单位ms")
    private Integer duration;

    @Schema(description = "红包生成速率:单位ms")
    private Integer generationRate;

    @Schema(description = "红包总金额:单位元")
    private Integer totalMoney;

    @Schema(description = "红包个数")
    private Integer redPackageNumber;

    @Schema(description = "红包雨活动开启时间")
    private Date date;

    @Schema(description = "红包雨活动标识")
    private String activityKey;
}
