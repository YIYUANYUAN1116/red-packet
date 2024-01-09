import http from '@/utils/http'

/**
 * @description 创建红包
 * @param totalMoney    红包金额
 * @param totalNumber    红包个数
 * @returns {Promise<ResultData<string>>} 红包key
 */
export function creatRedPacket(totalMoney: number, totalNumber: number){
    return http.get<string>(
        `/api/v1/send/${totalMoney}/${totalNumber}`,
    )
}

/**
 * @description 抢红包接口
 * @param redPackageKey   红包key
 */
export function getRedPacket(redPackageKey: string){
    return http.get(
        `api/v2/rob/${redPackageKey}`,
    )
}

/**
 * @description 获得奖励接口
 * @param redPackageKey  红包key
 * @returns {number} 获得奖励金额
 */
export function getRedPacketRecord(redPackageKey: string) {
    return http.get<number>(
        `/api/v2/record/${redPackageKey}`,
    )
}