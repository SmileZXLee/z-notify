import axios from 'axios'
import storage from 'store'
import notification from 'ant-design-vue/es/notification'
import { VueAxios } from './axios'
import { ACCESS_TOKEN } from '@/store/mutation-types'

// 创建 axios 实例
const request = axios.create({
  // API 请求的默认前缀
  baseURL: process.env.VUE_APP_API_BASE_URL,
  timeout: 30000 // 请求超时时间
})

// 异常拦截处理器
const errorHandler = (error) => {
  if (error.response.status === 401) {
    // 未登录或登录过期
    notification.error({
      message: 'Unauthorized',
      description: '登录已过期，请重新登录'
    })
    storage.remove(ACCESS_TOKEN)
    setTimeout(() => {
      window.location.reload()
    }, 500)
    return
  }
  notification.error({
    message: '请求失败'
  })
  return Promise.reject(error)
}

// request interceptor
request.interceptors.request.use(config => {
  const token = storage.get(ACCESS_TOKEN)
  // 如果 token 存在
  // 让每个请求携带自定义 token 请根据实际情况自行修改
  if (token) {
    config.headers[ACCESS_TOKEN] = token
  }
  return config
}, errorHandler)

// response interceptor
request.interceptors.response.use((response) => {
  const data = response.data
  if (data.status !== 0) {
    notification.error({
      message: data.message
    })
    return Promise.reject(data)
  }
  return data
}, errorHandler)

const installer = {
  vm: {},
  install (Vue) {
    Vue.use(VueAxios, request)
  }
}

export default request

export {
  installer as VueAxios,
  request as axios
}
