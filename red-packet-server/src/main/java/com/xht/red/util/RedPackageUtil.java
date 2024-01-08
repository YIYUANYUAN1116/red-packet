package com.xht.red.util;

import java.util.Random;

/**
 * @author: yzd
 * @create: 2024-01-08
 **/
public class RedPackageUtil {



    /**
     * 二倍均值算法
     * @author: yzd
     * @Date: 2024/1/8
     */
    public static Integer[] splitRedPackageAlgorithm(int totalMoney, int redPackageNumber) {
        Integer[] redPackages = new Integer[redPackageNumber];
        int useMoney = 0;
        for (int i = 0; i < redPackages.length; i++) {
            if (i == redPackageNumber-1){ //最后一个红包
                redPackages[i] = totalMoney-useMoney;
            }else {
                //二倍均值算法，每次拆分后塞进子红包的金额 = 随机区间(0,(剩余红包金额M ÷ 未被抢的剩余红包个数N) * 2)
                  int avgMoney = ((totalMoney-useMoney)/(redPackageNumber-i))*2;
                  redPackages[i] = 1+new Random().nextInt(avgMoney-1);//从0到bound随机返回一个数
            }
            useMoney = useMoney + redPackages[i];
        }

        return redPackages;
    }
}
