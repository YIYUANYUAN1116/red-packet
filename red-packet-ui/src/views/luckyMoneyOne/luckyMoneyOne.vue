<template>
    <div class="page-container">
        <van-button type="primary" size="large" @click="startRewardOverlay">展示上次获取的红包</van-button>
        
        <van-field label="持续时长">
            <template #input>
                <van-stepper v-model="duration" input-width="30vw" min="5000" step="1000"/>
            </template>    
        </van-field>
        <van-field label="生成速率">
      <template #input>
        <van-stepper v-model="generationRate" input-width="30vw" min="100" step="100"/>
      </template>
    </van-field>
    <van-field label="倒计时">
      <template #input>
        <van-stepper v-model="countdown" input-width="30vw" min="3000" step="1000"/>
      </template>
    </van-field>
    <van-field label="红包总金额">
      <template #input>
        <van-stepper v-model="luckyMoneyTotalMoney" input-width="30vw" min="1" step="1"/>
      </template>
    </van-field>
    <van-field label="有效红包个数">
      <template #input>
        <van-stepper v-model="luckyMoneyNumber" input-width="30vw" min="1" step="1"/>
      </template>
    </van-field>

    <van-button type="primary" size="large" @click="startLuckyMoneyActive">开启活动</van-button>
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
import LuckyMoney from "@/views/luckyMoneyOne/components/luckyMoney.vue";
import {ref, h, render} from "vue";
import CountdownMask from "@/components/countdownMask.vue";
import RewardMask from "@/components/rewardMask.vue";
import {creatRedPacket, getRedPacket} from "@/api/luckyMoney";
import {showToast} from "vant";

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


//#region <展示红包相关>
// 控制显示红包活动
const isShowLuckyMoneyMask = ref(false)

// 开启红包遮罩
function startLuckyMoneyMask(){
    isShowLuckyMoneyMask.value = true
}

// 关闭红包遮罩
function closeLuckyMoneyMask() {
  isShowLuckyMoneyMask.value = false
}
//#endregion


//#region <展示红包结束后奖品相关>
function startRewardOverlay(){
    // 判断是否开启了红包活动
  luckyMoneyKey.value ? rewardMaskRef.value?.show() : showToast('请先开启红包活动')
}


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

function startLuckyMoneyActive(){
     // 重置红包活动数据
  resetLuckyMoneyActiveData()
  // 开启倒计时遮罩层
  showCountdownMask()
  // 创建本次红包活动
  createLuckyMoneyActive()
}

// 重置红包活动数据
function resetLuckyMoneyActiveData() {
  luckyMoneyKey.value = ''
}

async function createLuckyMoneyActive() {
    const res = await creatRedPacket(
        luckyMoneyTotalMoney.value,
        luckyMoneyNumber.value,
    )
    luckyMoneyKey.value = res.data
}

//点击红包回调
async function clickLuckyMoneyCallback() {
    await getRedPacket(luckyMoneyKey.value)
}

//创建红包
function createLuckyMoney(){
  /**
   * 为什么不直接使用render函数将虚拟dom渲染到luckyMoneyContainerRef中呢？
   * 因为render函数将虚拟dom渲染到容器dom的时候，会将容器中的所有内容清空，导致我们之前添加的红包都消失不见。
   * 下方是采用方式：先创建一个div容器，将虚拟dom渲染到这个div容器中，这个时候这个div容器实际上结构已经和真实dom一样了，
   * 然后再将这个div容器的第一个子元素（就是我们创建的红包）使用原生的方式添加到luckyMoneyContainerRef容器中，这样就避免了上述的问题。
   * */
      // 使用h函数创建虚拟dom，再使用render函数将虚拟dom渲染为真实dom，最后将真实dom添加到容器中

  const container: HTMLDivElement = document.createElement(`div`)
  render(h(LuckyMoney,{
    clickCallback: clickLuckyMoneyCallback,
  }),container)

  const firstChild = container.firstElementChild
  firstChild && luckyMoneyContainerRef.value?.appendChild(firstChild)
}


function createLuckyMoneyInterval(duration: number,timeInterval: number){
  startLuckyMoneyMask();

  let timer: NodeJS.Timer | null = null;
  const startTiem =  new Date().getTime()
  console.log('开始掉落红包')
  timer = setInterval(()=>{
    //当前时间
    const now  = new Date().getTime()

    if(now - startTiem >= duration){
      clearInterval(timer as NodeJS.Timer)
      timer = null
      console.log(`结束掉落红包`)
      startRewardOverlay()
    }else{
      createLuckyMoney();
    }
  },timeInterval);

}

</script>

<style scoped lang="scss">
.page-container {
  width: 100vw;
  height: 100vh;
  background: #fff;
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
</style>