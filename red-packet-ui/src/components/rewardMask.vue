<template>
    <van-overlay :show="isShowMask" @click="close">
        <div class="wrapper" @click.stop>
            <div class="reward" v-if="amount">
                <span class="reward-description">恭喜获得</span>
                <div>
                    <span class="reward-amount">{{ amount }}</span>
                    <span class="reward-amount-description">元</span>
                </div>
            </div>
            <van-icon @click="close" class="reward-close" name="cross" color="#ea482d" size="10vw"/>
        </div>
    </van-overlay>
</template>

<script setup lang="ts">
import { getRedPacketRecord } from '@/api/luckyMoney';
import { ref } from 'vue';


const props = defineProps({
    closeCallback:{
        type: Function,
        default:()=>{

        }
    },
    luckyMoneyKey:{
        type: String,
        required: true
    }
})

const isShowMask = ref(false)
// 奖励金额
const amount = ref('')

//获取本次金额
async function getAmount() {
    const result = await getRedPacketRecord(props.luckyMoneyKey);
    amount.value = result.data ? result.data.toFixed(2) : '0.00'
}

function show(){
    getAmount()
    isShowMask.value = true
}

// 对外暴露的方法
defineExpose({
  show,
  close
})

function close(){
    isShowMask.value = false
    props.closeCallback && props.closeCallback()
    amount.value = ''
}
</script>

<style scoped lang="scss">
.wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-image: url('@/assets/images/reward.png');
  background-repeat: no-repeat;
  background-size: 70vw;
  /*位置居中*/
  background-position: center;

  .reward {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    transform: translateY(-70px);

    .reward-description {
      /*恭喜获得红包字体描述文字*/
      font-size: 20px;
      color: #68150d;
      font-weight: bold;
    }

    .reward-amount {
      /*红包金额*/
      font-size: 45px;
      color: #ea482d;
      font-weight: bold;
    }

    .reward-amount-description {
      /*红包金额描述文字*/
      font-size: 20px;
      color: #F05A6B;
      font-weight: bold;
    }
  }
  .reward-close{
    position: absolute;
    right: 2vw;
    top: 3vh;
  }
}
</style>