// 红包活动
export interface LuckyMoneyActive{
    // 红包雨活持续时长:单位ms
    duration: number
    // 红包生成速率:单位ms
    generationRate: number
    // 红包雨活动标识
    activityKey: string
    // 红包唯一key
    redPackageKey: string

    //红包总金额
    totalMoney: number

    //红包个数
    redPackageNumber: number

    //活动开启时间
    date: Date
}