<template>
    <div class="home-container">
      <van-button type="primary" size="large" to="/luckyMoneyOne">方式一: 组件写法</van-button>
      <van-button type="primary" size="large" to="/luckyMoneyTwo">方式二: 类的写法</van-button>
      <van-button type="primary" size="large" to="/luckyMoneyRain">方式三: 监听红包雨</van-button>
    </div>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
.home-container {
  display: flex; //设为Flex布局以后，子元素的float、clear和vertical-align属性将失效。
  flex-direction: column; //（决定主轴的方向（即项目的排列方向））
  align-items: center; //定义项目在竖直方向上对齐方式）
  justify-content: space-around; //定义项目在主轴上的对齐方式
  height: 26vh;
  margin: 3%;
}

</style>