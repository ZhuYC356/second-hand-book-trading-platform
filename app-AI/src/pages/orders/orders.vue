<template>
  <view class="orders-page">
    <app-header title="我的订单"></app-header>

    <!-- 我买到的 / 我卖出的 -->
    <view class="tabs">
      <view class="tab" :class="{ active: side === 'bought' }" @tap="switchSide('bought')">
        我买到的
        <view class="tab-line" v-if="side === 'bought'"></view>
      </view>
      <view class="tab" :class="{ active: side === 'sold' }" @tap="switchSide('sold')">
        我卖出的
        <view class="tab-line" v-if="side === 'sold'"></view>
      </view>
    </view>

    <!-- 状态筛选 -->
    <scroll-view class="filter" scroll-x :show-scrollbar="false">
      <view
        v-for="s in statusOptions"
        :key="s.value"
        class="filter-chip"
        :class="{ active: status === s.value }"
        @tap="switchStatus(s.value)"
      >{{ s.label }}</view>
    </scroll-view>

    <view class="list">
      <view v-if="list.length === 0 && !loading" class="empty">
        <view class="empty-icon">🧾</view>
        <view class="empty-text">暂无相关订单</view>
        <view class="empty-sub">{{ side === 'bought' ? '去首页淘一本好书吧' : '卖出图书后会在这里展示' }}</view>
      </view>

      <view v-for="order in list" :key="order.id" class="order-card" @tap="goBook(order.bookId)">
        <view class="card-head">
          <text class="order-no">单号 {{ order.orderNo }}</text>
          <view class="order-status" :class="'os-' + order.status">{{ statusText(order.status) }}</view>
        </view>
        <view class="card-body">
          <view class="cover">
            <image v-if="order.bookCover" :src="imgUrl(order.bookCover)" mode="aspectFill" class="cover-img" />
            <view v-else class="cover-ph">{{ (order.bookTitle || '书').charAt(0) }}</view>
          </view>
          <view class="info">
            <view class="title ellipsis-2">{{ order.bookTitle || '未知图书' }}</view>
            <view class="sub">{{ side === 'bought' ? '卖家：' + (order.sellerName || '-') : '买家：' + (order.buyerName || '-') }}</view>
            <view class="time">{{ fmtTime(order.createTime) }}</view>
          </view>
          <view class="price-col">
            <view class="price">
              <text class="symbol">¥</text>
              <text class="num">{{ fmtPrice(order.price) }}</text>
            </view>
          </view>
        </view>
        <view v-if="side === 'bought' && order.status === 'PENDING'" class="card-ops">
          <view class="op op-cancel" @tap.stop="cancelOrder(order)">取消订单</view>
          <view class="op op-pay" @tap.stop="payOrder(order)">去付款</view>
        </view>
        <view v-if="side === 'sold' && order.status === 'PENDING'" class="card-ops">
          <view class="wait-tip">等待买家付款</view>
        </view>
        <view v-if="order.status === 'COMPLETED' && order.finishTime" class="card-ops">
          <view class="done-tip">完成于 {{ fmtTime(order.finishTime) }}</view>
        </view>
      </view>

      <view v-if="list.length" class="load-more">
        <text v-if="loading">加载中...</text>
        <text v-else-if="finished">— 没有更多订单了 —</text>
      </view>
    </view>

    <app-tabbar current="orders" />
  </view>
</template>

<script>
import AppHeader from '@/components/app-header.vue'
import AppTabbar from '@/components/app-tabbar.vue'
import request from '@/utils/request'
import { imgUrl, fmtPrice, fmtTime, ORDER_STATUS, toast } from '@/utils/util'

const PAGE_SIZE = 10
const STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'PENDING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

export default {
  components: { AppHeader, AppTabbar },
  data() {
    return {
      side: 'bought',
      status: '',
      statusOptions: STATUS_OPTIONS,
      list: [],
      page: 1,
      finished: false,
      loading: false
    }
  },
  onShow() {
    this.reload()
  },
  onPullDownRefresh() {
    this.reload().finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    this.loadMore()
  },
  methods: {
    imgUrl,
    fmtPrice,
    fmtTime,
    statusText(status) {
      return (ORDER_STATUS[status] || {}).text || status
    },
    switchSide(side) {
      if (this.side === side) return
      this.side = side
      this.reload()
    },
    switchStatus(value) {
      if (this.status === value) return
      this.status = value
      this.reload()
    },
    async reload() {
      this.page = 1
      this.finished = false
      await this.fetchPage(true)
    },
    async loadMore() {
      if (this.loading || this.finished) return
      await this.fetchPage(false)
    },
    async fetchPage(reset) {
      if (this.loading) return
      this.loading = true
      try {
        const params = { page: this.page, size: PAGE_SIZE }
        if (this.status) params.status = this.status
        const pageData = await request({
          url: '/api/orders/' + this.side,
          data: params,
          loading: !reset // 首屏不弹全局 loading，避免下拉刷新时叠加
        })
        const records = pageData.records || []
        this.list = reset ? records : this.list.concat(records)
        this.finished = records.length < PAGE_SIZE
        this.page += 1
      } catch (e) {
        toast(e.message)
      } finally {
        this.loading = false
      }
    },
    payOrder(order) {
      uni.showModal({
        title: '确认付款',
        content: `确认支付 ¥${this.fmtPrice(order.price)} 购买《${order.bookTitle}》？`,
        success: async (res) => {
          if (!res.confirm) return
          try {
            await request({ url: `/api/orders/${order.id}/pay`, method: 'PUT' })
            toast('付款成功', 'success')
            this.reload()
          } catch (e) {
            toast(e.message)
          }
        }
      })
    },
    cancelOrder(order) {
      uni.showModal({
        title: '取消订单',
        content: `确定取消《${order.bookTitle}》的订单吗？`,
        confirmColor: '#ef4444',
        success: async (res) => {
          if (!res.confirm) return
          try {
            await request({ url: `/api/orders/${order.id}/cancel`, method: 'PUT' })
            toast('订单已取消', 'success')
            this.reload()
          } catch (e) {
            toast(e.message)
          }
        }
      })
    },
    goBook(bookId) {
      if (!bookId) return
      uni.navigateTo({ url: '/pages/detail/detail?id=' + bookId })
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding-bottom: 220rpx;
}

.tabs {
  display: flex;
  background: #fff;
  padding: 0 32rpx;
}
.tab {
  position: relative;
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  color: #6b7280;
  line-height: 96rpx;
}
.tab.active {
  color: #4f46e5;
  font-weight: 600;
}
.tab-line {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: 8rpx;
  width: 56rpx;
  height: 8rpx;
  border-radius: 8rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.filter {
  background: #fff;
  padding: 12rpx 24rpx 20rpx;
  white-space: nowrap;
  width: 100%;
  box-sizing: border-box;
}
.filter-chip {
  display: inline-block;
  padding: 0 30rpx;
  line-height: 58rpx;
  border-radius: 29rpx;
  font-size: 25rpx;
  color: #6b7280;
  background: #f3f4f6;
  margin-right: 16rpx;
}
.filter-chip.active {
  background: #eef2ff;
  color: #4f46e5;
  font-weight: 600;
}

.list {
  padding: 24rpx;
}
.order-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 26rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.card-head {
  display: flex;
  align-items: center;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f1f5;
}
.order-no {
  flex: 1;
  font-size: 22rpx;
  color: #9ca3af;
}
.order-status {
  font-size: 24rpx;
  font-weight: 600;
}
.os-PENDING {
  color: #ff5b2e;
}
.os-COMPLETED {
  color: #10b981;
}
.os-CANCELLED {
  color: #9ca3af;
}

.card-body {
  display: flex;
  padding-top: 22rpx;
}
.cover {
  width: 130rpx;
  height: 130rpx;
  border-radius: 14rpx;
  overflow: hidden;
  flex-shrink: 0;
  background: #eef0f6;
}
.cover-img {
  width: 100%;
  height: 100%;
}
.cover-ph {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
  font-size: 48rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
}
.info {
  flex: 1;
  margin-left: 22rpx;
  min-width: 0;
}
.title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.4;
}
.sub {
  margin-top: 8rpx;
  font-size: 23rpx;
  color: #9ca3af;
}
.time {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #c0c4cc;
}
.price-col {
  margin-left: 16rpx;
  flex-shrink: 0;
}
.price {
  color: #ff5b2e;
  font-weight: 700;
  text-align: right;
}
.symbol {
  font-size: 22rpx;
}
.num {
  font-size: 34rpx;
}

.card-ops {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f1f5;
}
.op {
  font-size: 25rpx;
  line-height: 62rpx;
  padding: 0 34rpx;
  border-radius: 31rpx;
  margin-left: 18rpx;
}
.op-cancel {
  background: #f6f7fb;
  color: #6b7280;
  border: 1rpx solid #e5e7eb;
}
.op-pay {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  box-shadow: 0 8rpx 20rpx rgba(79, 70, 229, 0.3);
}
.wait-tip,
.done-tip {
  font-size: 22rpx;
  color: #b6bac6;
}

.empty {
  padding: 140rpx 0 100rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.empty-icon {
  font-size: 90rpx;
}
.empty-text {
  margin-top: 20rpx;
  font-size: 30rpx;
  color: #6b7280;
}
.empty-sub {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #b6bac6;
}

.load-more {
  text-align: center;
  padding: 10rpx 0 30rpx;
  font-size: 24rpx;
  color: #b6bac6;
}
</style>
