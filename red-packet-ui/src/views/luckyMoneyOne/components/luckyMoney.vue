<template>
   <img class="luckyMoney" ref="luckyMoneyRef" @click="onclickHandle" v-bind="$attrs"
       src="../../../assets/images/luckyMoney.png" alt="">
</template>

<script setup lang="ts">
import { onMounted,reactive,ref } from 'vue';

const props = defineProps({
    clickCallback:{
        type: Function,
        default:()=>{

        }
    }
})

const luckyMoneyRef = ref<HTMLElement>()
// 下落轨道
const orbit = reactive([0, 15, 35, 55, 75])
// 下落轨道随机位置
const left = `${orbit[Math.round(Math.random() * 4)]}vw`
// 下落持续时间
const animationDuration = `${Math.random() * 3 + 2}s`
/**
 * 红包点击事件
 */
const onclickHandle = () => {
    destroy()
    props.clickCallback && props.clickCallback()
}

/**
 * 销毁红包
 */
 const destroy = () => {
  luckyMoneyRef.value?.remove()
}

/**
 * 定时销毁红包
 */
 const destroyByTime = (second: number) => {
  //     销毁图片
  setTimeout(() => {
    destroy()
  }, second * 1000)
}
onMounted(() => {
  //    定时销毁
  destroyByTime(Number(animationDuration.slice(0, -1)))
})

</script>

<style scoped lang="scss">
.luckyMoney {
  position: absolute;
  top: -75px;
  left: v-bind(left);
  width: 75px;
  animation: drop 2s linear;
  animation-duration: v-bind(animationDuration);

}

</style>