import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from './router'

const request = axios.create({ timeout: 15000 })

request.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body.code === 401) {
      localStorage.removeItem('userInfo')
      if (router.currentRoute.value.path !== '/login') {
        ElMessage.warning(body.msg || '请先登录')
        router.push('/login')
      }
      return Promise.reject(new Error(body.msg))
    }
    if (body.code !== 0) {
      ElMessage.error(body.msg || '操作失败')
      return Promise.reject(new Error(body.msg))
    }
    return body
  },
  () => {
    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(new Error('network error'))
  }
)

export default {
  get: (url, params) => request.get(url, { params }),
  post: (url, data) => request.post(url, data),
  put: (url, data) => request.put(url, data),
  del: (url) => request.delete(url)
}
