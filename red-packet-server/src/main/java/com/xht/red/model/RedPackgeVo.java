package com.xht.red.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Schema(description = "红包雨活动信息类")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RedPackgeVo {

    @Schema(description = "红包雨活持续时长:单位ms")
    private Integer duration;

    @Schema(description = "红包生成速率:单位ms")
    private Integer generationRate;

    @Schema(description = "红包雨标识")
    private String redPackageKey;

    @Schema(description = "红包雨活动标识")
    private String activityKey;
}
