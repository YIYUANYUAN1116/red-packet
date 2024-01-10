<template>
  <router-view></router-view>
</template>

<script setup lang="ts">
console.log(
    '%c Welcome to xht redPacket Rain! ',
    'color:#1e80ff;font-size:20px;background:#fff;padding:8px;',
)
// 生成token
localStorage.getItem('token') || localStorage.setItem('token', Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2))
</script>
