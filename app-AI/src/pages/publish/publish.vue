<template>
  <view class="publish-page">
    <app-header title="我的发布">
      <template #right>
        <view class="add-btn" @tap="goEdit()">+ 发布</view>
      </template>
    </app-header>

    <view class="list">
      <view v-if="list.length === 0 && !loading" class="empty">
        <view class="empty-icon">📖</view>
        <view class="empty-text">还没有发布过图书</view>
        <view class="empty-sub">点击右上角「+ 发布」上架你的第一本书</view>
        <button class="empty-btn" @tap="goEdit()">立即发布</button>
      </view>

      <view v-for="book in list" :key="book.id" class="item">
        <view class="cover">
          <image v-if="book.cover" :src="imgUrl(book.cover)" mode="aspectFill" class="cover-img" />
          <view v-else class="cover-ph" :style="{ background: placeholderBg(book.id) }">
            <text>{{ book.title.charAt(0) }}</text>
          </view>
        </view>
        <view class="info">
          <view class="row-1">
            <text class="title ellipsis-1">{{ book.title }}</text>
            <view class="status" :class="'st-' + book.status">{{ statusText(book.status) }}</view>
          </view>
          <view class="author ellipsis-1">{{ book.author || '佚名' }} · {{ book.categoryName || '未分类' }}</view>
          <view class="price-row">
            <view class="price">
              <text class="symbol">¥</text>
              <text class="num">{{ fmtPrice(book.price) }}</text>
            </view>
            <text class="time">{{ fmtDate(book.createTime) }}</text>
          </view>
          <view class="ops">
            <view class="op op-edit" @tap="goEdit(book.id)">编辑</view>
            <view v-if="book.status === 'ON_SALE'" class="op op-off" @tap="toggleStatus(book, 'OFF_SHELF')">下架</view>
            <view v-else-if="book.status === 'OFF_SHELF'" class="op op-on" @tap="toggleStatus(book, 'ON_SALE')">上架</view>
            <view class="op op-del" @tap="removeBook(book)">删除</view>
          </view>
        </view>
      </view>

      <view v-if="list.length" class="list-end">— 共 {{ total }} 本 —</view>
    </view>

    <app-tabbar current="publish" />
  </view>
</template>

<script>
import AppHeader from '@/components/app-header.vue'
import AppTabbar from '@/components/app-tabbar.vue'
import request from '@/utils/request'
import { imgUrl, fmtPrice, fmtDate, BOOK_STATUS, toast } from '@/utils/util'

export default {
  components: { AppHeader, AppTabbar },
  data() {
    return {
      list: [],
      total: 0,
      loading: false
    }
  },
  onShow() {
    this.load()
  },
  onPullDownRefresh() {
    this.load().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    imgUrl,
    fmtPrice,
    fmtDate,
    statusText(status) {
      return (BOOK_STATUS[status] || {}).text || status
    },
    placeholderBg(id) {
      const colors = [
        'linear-gradient(135deg,#667eea,#764ba2)',
        'linear-gradient(135deg,#f093fb,#f5576c)',
        'linear-gradient(135deg,#4facfe,#00f2fe)',
        'linear-gradient(135deg,#43e97b,#38f9d7)',
        'linear-gradient(135deg,#fa709a,#fee140)',
        'linear-gradient(135deg,#30cfd0,#330867)'
      ]
      return colors[(id || 0) % colors.length]
    },
    async load() {
      this.loading = true
      try {
        const pageData = await request({ url: '/api/books/my', data: { page: 1, size: 100 }, loading: false })
        this.list = pageData.records || []
        this.total = pageData.total || 0
      } catch (e) {
        toast(e.message)
      } finally {
        this.loading = false
      }
    },
    goEdit(id) {
      uni.navigateTo({ url: '/pages/edit/edit' + (id ? '?id=' + id : '') })
    },
    async toggleStatus(book, status) {
      try {
        await request({ url: `/api/books/${book.id}/status?status=${status}`, method: 'PUT' })
        toast(status === 'ON_SALE' ? '已上架' : '已下架', 'success')
        this.load()
      } catch (e) {
        toast(e.message)
      }
    },
    removeBook(book) {
      uni.showModal({
        title: '删除确认',
        content: `确定删除《${book.title}》吗？`,
        confirmColor: '#ef4444',
        success: async (res) => {
          if (!res.confirm) return
          try {
            await request({ url: '/api/books/' + book.id, method: 'DELETE' })
            toast('删除成功', 'success')
            this.load()
          } catch (e) {
            toast(e.message)
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.publish-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding-bottom: 220rpx;
}

.add-btn {
  color: #fff;
  font-size: 26rpx;
  padding: 8rpx 24rpx;
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.2);
  border: 1rpx solid rgba(255, 255, 255, 0.4);
}

.list {
  padding: 24rpx;
}
.item {
  display: flex;
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.cover {
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
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
  font-size: 64rpx;
  font-weight: 700;
}
.info {
  flex: 1;
  margin-left: 24rpx;
  min-width: 0;
}
.row-1 {
  display: flex;
  align-items: center;
}
.title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
  margin-right: 12rpx;
}
.status {
  flex-shrink: 0;
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
}
.st-ON_SALE {
  background: #ecfdf5;
  color: #10b981;
}
.st-OFF_SHELF {
  background: #f3f4f6;
  color: #9ca3af;
}
.st-SOLD {
  background: #fff7ed;
  color: #f59e0b;
}
.author {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #9ca3af;
}
.price-row {
  margin-top: 10rpx;
  display: flex;
  align-items: baseline;
}
.price {
  color: #ff5b2e;
  font-weight: 700;
}
.symbol {
  font-size: 22rpx;
}
.num {
  font-size: 34rpx;
}
.time {
  margin-left: auto;
  font-size: 22rpx;
  color: #c0c4cc;
}
.ops {
  margin-top: 16rpx;
  display: flex;
  justify-content: flex-end;
}
.op {
  font-size: 24rpx;
  line-height: 56rpx;
  padding: 0 26rpx;
  border-radius: 28rpx;
  margin-left: 16rpx;
  background: #f6f7fb;
  color: #4b5563;
  border: 1rpx solid #e5e7eb;
}
.op-on {
  background: #eef2ff;
  color: #4f46e5;
  border-color: #e0e7ff;
}
.op-del {
  background: #fef2f2;
  color: #ef4444;
  border-color: #fee2e2;
}
.op-edit {
  background: #fff;
}

/* 空状态 */
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
.empty-btn {
  margin-top: 40rpx;
  width: 320rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 30rpx;
}

.list-end {
  text-align: center;
  padding: 10rpx 0 30rpx;
  font-size: 24rpx;
  color: #b6bac6;
}
</style>
