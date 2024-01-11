import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";
import { error } from "console";
import { showToast } from "vant";
import { ResultData } from "./type";


export const service: AxiosInstance = axios.create({
    //不同环境设不同的baseURL
    baseURL:import.meta.env.VITE_APP_BASE_API,
    timeout: 25000,
})

/**
 * 请求拦截器
 */

service.interceptors.request.use(
    (config) => {
        let token = localStorage.getItem('token');
        if (!token) {
            localStorage.setItem('token', Math.random().toString(36).substring(2) + Math.random().toString(36).substring(2))
            token = localStorage.getItem('token')
        }
        if (token) {
            config.headers.token = token
        }
        return config
    },
    (error: AxiosError) => {
        showToast(error.message)
        return Promise.reject(error)
    },
)


/**
 * @description: 响应拦截器
 * @returns {*}
 */
service.interceptors.response.use(
    (response: AxiosResponse) => {
        const {data} = response
        return data
    },
    (error: AxiosError) => {
        showToast('请求失败')
        return Promise.reject(error)
    },
)

/**
 * @description: 导出封装的请求方法
 * @returns {*}
 */
const http = {
    
    get<T>(
        url: string,
        params?: object,
        config?: AxiosRequestConfig,
    ):Promise<ResultData<T>>{
        return service.get(url,{params,...config})
    },

    post<T>(
        url: string,
        data?: object,
        config?: AxiosRequestConfig,
    ):Promise<ResultData<T>>{
        return service.post(url,data,config)
    },
    
    put<T>(
        url: string,
        data?: object,
        config?: AxiosRequestConfig,
    ): Promise<ResultData<T>> {
        return service.put(url, data, config)
    },

    delete<T>(
        url: string,
        data?: object,
        config?: AxiosRequestConfig,
    ): Promise<ResultData<T>> {
        return service.delete(url, {data, ...config})
    },
}

export default http