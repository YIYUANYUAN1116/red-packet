import { createApp } from 'vue'
import "./style/index.scss"
import App from './App.vue'

//引入样式
import '@/style/index.scss'

// 引入插件
import '@/plugIn'

// 引入路由
import router from '@/router'

const app = createApp(App)
app.use(router)
app.mount('#app')


