import config from './config'

/** 后端图片相对地址补全为完整 URL；空值返回 '' */
export function imgUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//.test(path)) return path
  return config.BASE_URL + path
}

/** 价格格式化，保留两位小数 */
export function fmtPrice(v) {
  const n = Number(v)
  if (isNaN(n)) return '0.00'
  return n.toFixed(2)
}

/** 时间格式化：2026-10-01T12:00:00 / 2026-10-01 12:00:00 -> 10-01 12:00 */
export function fmtTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(5, 16)
}

export function fmtDate(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 10)
}

/** 图书状态映射 */
export const BOOK_STATUS = {
  ON_SALE: { text: '在售', type: 'success' },
  OFF_SHELF: { text: '已下架', type: 'info' },
  SOLD: { text: '已售出', type: 'warning' }
}

/** 订单状态映射 */
export const ORDER_STATUS = {
  PENDING: { text: '待付款' },
  COMPLETED: { text: '已完成' },
  CANCELLED: { text: '已取消' }
}

/** 延迟，用于下拉刷新等场景 */
export function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

/** 轻提示 */
export function toast(title, icon = 'none') {
  uni.showToast({ title, icon, mask: true })
}
