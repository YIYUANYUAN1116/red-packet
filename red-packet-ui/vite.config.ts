import vue from '@vitejs/plugin-vue';
import { defineConfig, loadEnv } from 'vite'
import Components from 'unplugin-vue-components/vite';
import {VantResolver} from 'unplugin-vue-components/resolvers';
import path from 'path';


export default defineConfig((config) => {
    const { mode } = config
    const env = loadEnv(mode, process.cwd())

    return {
        plugins: [
            vue(),
            Components({
                resolvers: [VantResolver()],
            }),
        ],
        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'src'),
            },
        },
        server: {
            host: 'localhost',
            port: Number(env.VITE_APP_PORT),
            proxy: {
                [env.VITE_APP_BASE_API]: {
                    target: env.VITE_APP_BASE_URL,
                    changeOrigin: true,
                    rewrite: (path) => path.replace( new RegExp(`^${env.VITE_APP_BASE_API}`), ''),
                },
            },
        },
    }
})
