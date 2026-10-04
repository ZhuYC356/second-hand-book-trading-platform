<template>
  <view class="detail-page">
    <!-- 返回按钮 -->
    <view class="nav-back" :style="{ top: (statusBarHeight + 12) + 'px' }" @tap="goBack">
      <view class="arrow"></view>
    </view>

    <!-- 顶部封面区 -->
    <view class="hero" :style="{ background: heroBg }">
      <image v-if="book.cover" :src="imgUrl(book.cover)" mode="aspectFill" class="hero-img" />
      <view v-else class="hero-char">{{ book.title ? book.title.charAt(0) : '' }}</view>
      <view class="hero-fade"></view>
    </view>

    <!-- 上浮信息卡 -->
    <view class="content">
      <view class="price-bar">
        <view class="price">
          <text class="symbol">¥</text>
          <text class="num">{{ fmtPrice(book.price) }}</text>
        </view>
        <text v-if="book.originalPrice" class="original">原价 ¥{{ fmtPrice(book.originalPrice) }}</text>
        <view v-if="book.originalPrice" class="discount">省 {{ discountPercent }}</view>
        <view class="cond">{{ book.conditionLevel }}</view>
      </view>

      <view class="title">{{ book.title }}</view>

      <view class="meta-list">
        <view class="meta-row"><text class="meta-label">作者</text><text class="meta-value">{{ book.author || '佚名' }}</text></view>
        <view class="meta-row"><text class="meta-label">分类</text><text class="meta-value">{{ book.categoryName || '-' }}</text></view>
        <view class="meta-row" v-if="book.isbn"><text class="meta-label">ISBN</text><text class="meta-value">{{ book.isbn }}</text></view>
        <view class="meta-row"><text class="meta-label">发布时间</text><text class="meta-value">{{ fmtDate(book.createTime) }}</text></view>
      </view>

      <view class="seller">
        <view class="seller-avatar">{{ (book.sellerName || '卖').charAt(0) }}</view>
        <view class="seller-info">
          <view class="seller-name">{{ book.sellerName || '匿名卖家' }}</view>
          <view class="seller-desc">好评卖家 · 邮寄/面交均可</view>
        </view>
        <view class="seller-badge">信用良好</view>
      </view>

      <view class="desc-block">
        <view class="desc-title">宝贝描述</view>
        <view class="desc-text">{{ book.description || '暂无描述' }}</view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="footer-safe">
      <view class="footer">
        <view class="total">
          <text class="total-label">价格</text>
          <view class="total-price">
            <text class="symbol">¥</text>
            <text class="num">{{ fmtPrice(book.price) }}</text>
          </view>
        </view>
        <button class="buy-btn" :disabled="book.status !== 'ON_SALE' || buying" @tap="buy">
          {{ book.status === 'SOLD' ? '已售出' : book.status === 'OFF_SHELF' ? '已下架' : (buying ? '下单中...' : '立即购买') }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request'
import { imgUrl, fmtPrice, fmtDate, toast } from '@/utils/util'

export default {
  data() {
    return {
      id: 0,
      book: {},
      buying: false,
      statusBarHeight: 0
    }
  },
  computed: {
    heroBg() {
      const colors = [
        'linear-gradient(135deg,#667eea,#764ba2)',
        'linear-gradient(135deg,#f093fb,#f5576c)',
        'linear-gradient(135deg,#4facfe,#00f2fe)',
        'linear-gradient(135deg,#43e97b,#38f9d7)',
        'linear-gradient(135deg,#fa709a,#fee140)',
        'linear-gradient(135deg,#30cfd0,#330867)'
      ]
      return colors[(this.id || 0) % colors.length]
    },
    discountPercent() {
      const p = Number(this.book.price)
      const o = Number(this.book.originalPrice)
      if (!o || isNaN(p) || p >= o) return ''
      return Math.round((1 - p / o) * 100) + '%'
    }
  },
  onLoad(options) {
    this.id = Number(options.id || 0)
    try {
      const info = uni.getSystemInfoSync()
      this.statusBarHeight = info.statusBarHeight || 0
    } catch (e) {
      this.statusBarHeight = 0
    }
    this.load()
  },
  methods: {
    imgUrl,
    fmtPrice,
    fmtDate,
    async load() {
      try {
        this.book = await request({ url: '/api/books/' + this.id })
      } catch (e) {
        toast(e.message)
        setTimeout(() => uni.navigateBack(), 800)
      }
    },
    async buy() {
      if (this.buying) return
      this.buying = true
      try {
        await request({ url: '/api/orders/buy', method: 'POST', data: { bookId: this.id } })
        toast('下单成功，请前往订单页付款', 'success')
        setTimeout(() => {
          uni.switchTab({ url: '/pages/orders/orders' })
        }, 800)
      } catch (e) {
        toast(e.message)
      } finally {
        this.buying = false
      }
    },
    goBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/home/home' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding-bottom: 180rpx;
}

/* 返回按钮 */
.nav-back {
  position: fixed;
  left: 28rpx;
  z-index: 20;
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}
.arrow {
  width: 20rpx;
  height: 20rpx;
  border-left: 5rpx solid #1f2937;
  border-bottom: 5rpx solid #1f2937;
  transform: rotate(45deg);
  margin-left: 8rpx;
}

/* 封面区 */
.hero {
  height: 620rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.hero-img {
  width: 100%;
  height: 100%;
}
.hero-char {
  color: rgba(255, 255, 255, 0.9);
  font-size: 200rpx;
  font-weight: 700;
}
.hero-fade {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 120rpx;
  background: linear-gradient(to bottom, rgba(246, 247, 251, 0), rgba(246, 247, 251, 1));
}

/* 信息卡 */
.content {
  margin: -40rpx 24rpx 0;
  position: relative;
  z-index: 2;
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx;
  box-shadow: 0 10rpx 40rpx rgba(31, 41, 55, 0.08);
}
.price-bar {
  display: flex;
  align-items: baseline;
}
.price {
  color: #ff5b2e;
  font-weight: 700;
}
.symbol {
  font-size: 30rpx;
}
.num {
  font-size: 52rpx;
}
.original {
  margin-left: 16rpx;
  font-size: 24rpx;
  color: #c0c4cc;
  text-decoration: line-through;
}
.discount {
  margin-left: 16rpx;
  padding: 4rpx 14rpx;
  background: #fff1eb;
  color: #ff5b2e;
  font-size: 22rpx;
  border-radius: 8rpx;
}
.cond {
  margin-left: auto;
  padding: 8rpx 20rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 24rpx;
  border-radius: 24rpx 8rpx 24rpx 8rpx;
}

.title {
  margin-top: 20rpx;
  font-size: 38rpx;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.4;
}

.meta-list {
  margin-top: 28rpx;
  background: #f8f9fc;
  border-radius: 20rpx;
  padding: 8rpx 24rpx;
}
.meta-row {
  display: flex;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f0f1f5;
}
.meta-row:last-child {
  border-bottom: none;
}
.meta-label {
  width: 140rpx;
  color: #9ca3af;
  font-size: 26rpx;
}
.meta-value {
  flex: 1;
  color: #1f2937;
  font-size: 26rpx;
}

/* 卖家 */
.seller {
  margin-top: 28rpx;
  display: flex;
  align-items: center;
}
.seller-avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}
.seller-info {
  margin-left: 20rpx;
  flex: 1;
}
.seller-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
}
.seller-desc {
  margin-top: 4rpx;
  font-size: 22rpx;
  color: #9ca3af;
}
.seller-badge {
  padding: 6rpx 18rpx;
  border-radius: 20rpx;
  background: #ecfdf5;
  color: #10b981;
  font-size: 22rpx;
}

/* 描述 */
.desc-block {
  margin-top: 28rpx;
}
.desc-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
}
.desc-text {
  margin-top: 14rpx;
  font-size: 27rpx;
  color: #6b7280;
  line-height: 1.7;
}

/* 底部操作栏 */
.footer-safe {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  background: #fff;
  box-shadow: 0 -6rpx 24rpx rgba(31, 41, 55, 0.06);
  z-index: 10;
}
.footer {
  height: 120rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
}
.total {
  flex: 1;
  display: flex;
  align-items: baseline;
}
.total-label {
  font-size: 24rpx;
  color: #9ca3af;
  margin-right: 12rpx;
}
.total-price {
  color: #ff5b2e;
  font-weight: 700;
}
.total-price .num {
  font-size: 44rpx;
}
.buy-btn {
  width: 280rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: 0 10rpx 24rpx rgba(79, 70, 229, 0.35);
}
.buy-btn[disabled] {
  background: #d8dbe6;
  color: #fff;
  box-shadow: none;
}
</style>
