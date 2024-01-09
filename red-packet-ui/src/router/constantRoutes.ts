import { RouteRecordRaw } from "vue-router"; //outeRecordRaw 是 Vue Router 4.x 中新增的类型，用于定义路由配置。它是一个 TypeScript 类型

export const staticRoutes : RouteRecordRaw[] = [
    {
        path: '/luckyMoneyOne',
        name: 'luckyMoneyOne',
        component: () => import('@/views/luckyMoneyOne/luckyMoneyOne.vue'),
        meta: {
            title: '方式一',
        },
    },
    {
        path: '/luckyMoneyTwo',
        name: 'luckyMoneyTwo',
        component: () => import('@/views/luckyMoneyTwo/luckyMoneyTwo.vue'),
        meta: {
            title: '方式二',
        },
    },
    {
        path: '/luckyMoneyRain',
        name: 'luckyMoneyRain',
        component: () => import('@/views/luckyMoneyRain/luckyMoneyRain.vue'),
        meta: {
            title: '红包雨',
        },
    },
    {
        path: '/',
        name: 'home',
        component: () => import('@/views/home/home.vue'),
        meta: {
            title: '首页',
        },
    },

]