import config from './config'

/**
 * 统一请求封装
 * 后端返回结构：{ code: 0成功|1业务错|401未登录|403无权限, msg, data }
 * 鉴权方式：Session + Cookie
 *  - H5：同源请求，浏览器自动携带 Cookie，无需处理
 *  - 小程序/App：无 Cookie 机制，手动从响应头取 Set-Cookie 存储，并在请求头带上 Cookie
 */
const COOKIE_KEY = 'APP_SESSION_COOKIE'

function readCookie() {
  return uni.getStorageSync(COOKIE_KEY) || ''
}

function saveCookie(setCookieVal) {
  if (!setCookieVal) return
  const items = Array.isArray(setCookieVal) ? setCookieVal : [setCookieVal]
  let cookie = readCookie()
  for (const item of items) {
    const pair = String(item).split(';')[0]
    const name = pair.split('=')[0]
    if (!name) continue
    const reg = new RegExp('(^|;\\s*)' + name.trim() + '=[^;]*')
    if (reg.test(cookie)) {
      cookie = cookie.replace(reg, '$1' + pair)
    } else {
      cookie = cookie ? cookie + '; ' + pair : pair
    }
  }
  uni.setStorageSync(COOKIE_KEY, cookie)
}

function getHeader(headerObj, name) {
  if (!headerObj) return ''
  const target = name.toLowerCase()
  for (const key of Object.keys(headerObj)) {
    if (key.toLowerCase() === target) return headerObj[key]
  }
  return ''
}

function buildHeader(header = {}) {
  // #ifndef H5
  const cookie = readCookie()
  if (cookie) header['Cookie'] = cookie
  // #endif
  return header
}

function handle401() {
  uni.removeStorageSync(COOKIE_KEY)
  uni.removeStorageSync('APP_USER')
  uni.reLaunch({ url: '/pages/login/login' })
}

/** 处理业务响应，成功返回 data，失败抛出 Error */
function handleResponse(res) {
  const body = res.data || {}
  if (body.code === 0) return body.data
  if (body.code === 401) {
    handle401()
    throw new Error('登录已失效，请重新登录')
  }
  throw new Error(body.msg || '请求失败')
}

/**
 * 发起请求
 * @param {object} options { url, method, data, loading }
 * @returns {Promise<any>} 成功时 resolve 业务 data
 */
export function request(options) {
  const { url, method = 'GET', data, loading = true } = options
  if (loading) uni.showLoading({ title: '加载中', mask: true })
  return new Promise((resolve, reject) => {
    uni.request({
      url: config.BASE_URL + url,
      method,
      data,
      header: buildHeader(),
      success: (res) => {
        // 非 H5 端手动保存会话 Cookie
        // #ifndef H5
        const setCookie = getHeader(res.header, 'Set-Cookie')
        if (setCookie) saveCookie(setCookie)
        // #endif
        try {
          resolve(handleResponse(res))
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '网络异常，请稍后重试'))
      },
      complete: () => {
        if (loading) uni.hideLoading()
      }
    })
  })
}

/** 上传文件（返回业务 data，如封面地址 /upload/xxx.jpg） */
export function uploadFile(filePath) {
  uni.showLoading({ title: '上传中', mask: true })
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: config.BASE_URL + '/api/upload',
      filePath,
      name: 'file',
      header: buildHeader(),
      success: (res) => {
        // #ifndef H5
        const setCookie = getHeader(res.header, 'Set-Cookie')
        if (setCookie) saveCookie(setCookie)
        // #endif
        let body = {}
        try {
          body = JSON.parse(res.data)
        } catch (e) {
          reject(new Error('上传响应解析失败'))
          return
        }
        if (body.code === 0) {
          resolve(body.data)
        } else if (body.code === 401) {
          handle401()
          reject(new Error('登录已失效，请重新登录'))
        } else {
          reject(new Error(body.msg || '上传失败'))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '上传失败'))
      },
      complete: () => {
        uni.hideLoading()
      }
    })
  })
}

export default request
