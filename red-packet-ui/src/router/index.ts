import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { staticRoutes } from './constantRoutes'

const router = createRouter({
    history:createWebHashHistory(),//它在内部传递的实际 URL 之前使用了一个哈希字符（#）
    routes: staticRoutes as RouteRecordRaw[],
    // 刷新时，滚动条位置还原
    scrollBehavior: () => ({ left: 0, top: 0 }),
})

export default router;