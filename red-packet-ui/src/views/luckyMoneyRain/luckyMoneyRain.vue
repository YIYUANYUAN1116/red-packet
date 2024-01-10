<template>
    <div class="page-container">
        <van-button type="danger" size="large">
        <van-swipe
          vertical
          class="notice-swipe"
          :autoplay="2000"
          :touchable="false"
          :show-indicators="false"
      >
        <van-swipe-item>不要走开</van-swipe-item>
        <van-swipe-item>红包马上降临</van-swipe-item>
        </van-swipe>
        </van-button>
        <van-button type="primary" size="large" @click="startRewardOverlay">展示上次获取的红包</van-button>
    </div>
      <!--  红包活动容器-->
  <div v-if="isShowLuckyMoneyMask" class="lucky-money-container" ref="luckyMoneyContainerRef"></div>
  <!--  奖品遮罩层-->
  <reward-mask ref="rewardMaskRef" :closeCallback="closeLuckyMoneyMask" :luckyMoneyKey="luckyMoneyKey"></reward-mask>
  <!--  倒计时遮罩层-->
  <countdown-mask ref="countdownMaskRef" :countdown="countdown"
                  :countDownFinishCallback="countDownFinishCallback"></countdown-mask>

</template>

<script setup lang="ts">
import LuckyMoneyClass from './luckyMoneyClass'
import {onMounted, ref} from "vue";
import CountdownMask from "@/components/countdownMask.vue";
import RewardMask from "@/components/rewardMask.vue";
import {creatRedPacket, getRedPacket,addRedPacket} from "@/api/luckyMoney";
import {datePickerProps, showToast} from "vant";
import useWebsocket from "@/hooks/useWebsocket";
import {LuckyMoneyActive} from "@/api/luckyMoney/types.ts";
import moment from "moment";

const luckyMoneyContainerRef = ref<HTMLImageElement>()
const rewardMaskRef = ref<InstanceType<typeof RewardMask>>()

//#region <创建红包相关>
// 持续时长 单位ms
const duration = ref(5000)
// 红包生成速率 单位ms  200ms生成一个红包
const generationRate = ref(200)
// 红包总金额 单位元
const luckyMoneyTotalMoney = ref(100)
// 真实红包个数（有效红包个数）
const luckyMoneyNumber = ref(10)
// 本次红包活动的唯一key值
const luckyMoneyKey = ref('')
//#endregion

//活动10秒后开始
const luckyMoneyDate = () =>{
  var now = moment();
  now.add(100, 's');
  return now.valueOf()
}



//#region <展示红包相关>
// 控制显示红包活动
const isShowLuckyMoneyMask = ref(false)


//红包活动10秒后开始



// 开启红包遮罩
function startLuckyMoneyMask() {
  isShowLuckyMoneyMask.value = true
}

// 关闭红包遮罩
function closeLuckyMoneyMask() {
  isShowLuckyMoneyMask.value = false
}

//#endregion


// 开启奖品遮罩层
function startRewardOverlay() {
  // 判断是否开启了红包活动
  luckyMoneyKey.value ? rewardMaskRef.value?.show() : showToast('请先开启红包活动')
}


//#endregion

//#region <倒计时相关>
const countdownMaskRef = ref<InstanceType<typeof CountdownMask>>()
// 倒计时时间 单位ms
const countdown = ref(4000)

// 倒计时结束回调
function countDownFinishCallback() {
  createLuckyMoneyInterval(duration.value, generationRate.value)
}

//开启倒计时遮罩层
function showCountdownMask() {
  countdownMaskRef.value?.show()
}

//#endregion


// 开启红包活动
function startLuckyMoneyActive() {
  // 重置红包活动数据
  resetLuckyMoneyActiveData()
  // // 开启倒计时遮罩层
  // showCountdownMask()
  // 创建本次红包活动
  createLuckyMoneyActive()
}

function resetLuckyMoneyActiveData() {
  luckyMoneyKey.value = ''
}

// 创建本次红包活动
async function createLuckyMoneyActive() {
  const luckyMoneyActive : LuckyMoneyActive = {
    duration: duration.value,
    generationRate: generationRate.value,
    activityKey: '',
    redPackageKey: '',
    totalMoney: luckyMoneyTotalMoney.value,
    redPackageNumber: luckyMoneyNumber.value,
    //活动开启时间
    date: luckyMoneyDate()
    }

    const res = await addRedPacket(luckyMoneyActive)
    luckyMoneyKey.value = res.data
    if(luckyMoneyKey.value){
        const{connect,disconnect} = useWebsocket(`ws://localhost:8888/api/websocket/${luckyMoneyKey.value}/${localStorage.getItem('token')}`,
            (res: LuckyMoneyActive) => {
                duration.value = res.duration
                generationRate.value = res.generationRate
                luckyMoneyKey.value = res.redPackageKey
                autoStartLuckyMoneyActive()
                // 3s后断开连接
                setTimeout(() => {
                    disconnect()
                }, 3000)
            });
            connect(); 
    }else{
        showToast('缺少必要红包参数')
    }
}

// 点击红包的回调
async function clickLuckyMoneyCallback() {
  await getRedPacket(luckyMoneyKey.value)
}

// 创建红包
function createLuckyMoney() {
  luckyMoneyContainerRef.value && new LuckyMoneyClass({
    parent: luckyMoneyContainerRef.value,
    clickCallback: clickLuckyMoneyCallback
  })
}

/**
 * 定时创建红包
 * @param duration 持续时长 单位ms
 * @param timeInterval 红包生成速率 单位ms
 */

function createLuckyMoneyInterval(duration: number, timeInterval: number){
     // 打开红包遮罩
  startLuckyMoneyMask()
  let timer:NodeJS.Timer|null=null
  const startTime = new Date().getTime()
  console.log('开始掉落红包')
  timer = setInterval(()=>{
    const now = new Date().getTime()
    if(now - startTime >= duration){
        clearInterval(timer as NodeJS.Timer)
        timer = null
        console.log('结束掉落红包')
        startRewardOverlay()
    }else{
        // 创建红包
      createLuckyMoney()
    }
  },timeInterval)
}

//#region <webSocket自动执行任务相关>
function autoStartLuckyMoneyActive() {
  // 开启倒计时遮罩层
  showCountdownMask()

}

onMounted(()=>{
  startLuckyMoneyActive();
})


</script>

<style scoped lang="scss">
.page-container {
  display: flex;
  flex-direction:column;
  background: #fff;
  align-items: center; //定义项目在竖直方向上对齐方式）
  justify-content: space-around; //定义项目在主轴上的对齐方式
  height: 26vh;
  margin: 3%;
}

.lucky-money-container {
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  /*红包雨背景颜色，渐变，微透明*/
  background: linear-gradient(180deg, rgba(255, 215, 0, 1) 0%, rgba(255, 165, 0, 1) 100%);
}

.notice-swipe {
  height: 40px;
  line-height: 40px;
}
</style>