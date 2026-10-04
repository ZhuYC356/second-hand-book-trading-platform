<template>
  <view class="home-page">
    <!-- 渐变头部：标题 + 搜索 + 分类 -->
    <view class="head">
      <view class="head-statusbar"></view>
      <view class="head-top">
        <view>
          <view class="head-title">书遇</view>
          <view class="head-sub">旧书不旧，遇上方知有</view>
        </view>
        <view class="head-avatar" @tap="goProfile">书</view>
      </view>

      <!-- 搜索框 -->
      <view class="search">
        <view class="search-icon"></view>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索书名 / 作者 / ISBN"
          placeholder-class="ph"
          confirm-type="search"
          @confirm="onSearch"
        />
        <view v-if="keyword" class="search-clear" @tap="clearKeyword">×</view>
        <view class="search-btn" @tap="onSearch">搜索</view>
      </view>

      <!-- 分类横滑 -->
      <scroll-view class="cats" scroll-x :show-scrollbar="false">
        <view
          v-for="c in catList"
          :key="c.id"
          class="cat-chip"
          :class="{ active: categoryId === c.id }"
          @tap="selectCat(c.id)"
        >{{ c.name }}</view>
      </scroll-view>
      <view class="head-bottom-radius"></view>
    </view>

    <!-- 图书列表：双列瀑布 -->
    <view class="list-wrap">
      <view v-if="list.length === 0 && !loading" class="empty">
        <view class="empty-icon">📚</view>
        <view class="empty-text">没有找到相关图书</view>
        <view class="empty-sub">换个关键词或分类试试吧</view>
      </view>

      <view class="grid">
        <view
          v-for="book in list"
          :key="book.id"
          class="book-card"
          @tap="goDetail(book.id)"
        >
          <view class="cover">
            <image v-if="book.cover" :src="imgUrl(book.cover)" mode="aspectFill" class="cover-img" />
            <view v-else class="cover-placeholder" :style="{ background: placeholderBg(book.id) }">
              <text class="cover-char">{{ book.title.charAt(0) }}</text>
            </view>
            <view class="cond-tag">{{ book.conditionLevel }}</view>
          </view>
          <view class="info">
            <view class="title ellipsis-2">{{ book.title }}</view>
            <view class="author ellipsis-1">{{ book.author || '佚名' }}</view>
            <view class="price-row">
              <view class="price">
                <text class="symbol">¥</text>
                <text class="num">{{ fmtPrice(book.price) }}</text>
              </view>
              <text v-if="book.originalPrice" class="original">¥{{ fmtPrice(book.originalPrice) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="list.length" class="load-more">
        <text v-if="loading">加载中...</text>
        <text v-else-if="finished">— 已经到底啦 —</text>
      </view>
    </view>

    <app-tabbar current="home" />
  </view>
</template>

<script>
import request from '@/utils/request'
import AppTabbar from '@/components/app-tabbar.vue'
import { imgUrl, fmtPrice } from '@/utils/util'

const PAGE_SIZE = 8

export default {
  components: { AppTabbar },
  data() {
    return {
      keyword: '',
      categoryId: 0,
      catList: [{ id: 0, name: '全部' }],
      list: [],
      page: 1,
      finished: false,
      loading: false
    }
  },
  onLoad() {
    this.loadCats()
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
    async loadCats() {
      try {
        const cats = await request({ url: '/api/categories', loading: false })
        this.catList = [{ id: 0, name: '全部' }, ...(cats || [])]
      } catch (e) {
        /* 分类加载失败不阻塞首页 */
      }
    },
    selectCat(id) {
      if (this.categoryId === id) return
      this.categoryId = id
      this.reload()
    },
    onSearch() {
      this.reload()
    },
    clearKeyword() {
      this.keyword = ''
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
        if (this.keyword.trim()) params.keyword = this.keyword.trim()
        if (this.categoryId) params.categoryId = this.categoryId
        const pageData = await request({ url: '/api/books/page', data: params, loading: false })
        const records = pageData.records || []
        this.list = reset ? records : this.list.concat(records)
        this.finished = records.length < PAGE_SIZE
        this.page += 1
      } catch (e) {
        uni.showToast({ title: e.message, icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    placeholderBg(id) {
      const colors = [
        'linear-gradient(135deg,#667eea,#764ba2)',
        'linear-gradient(135deg,#f093fb,#f5576c)',
        'linear-gradient(135deg,#4facfe,#00f2fe)',
        'linear-gradient(135deg,#43e97b,#38f9d7)',
        'linear-gradient(135deg,#fa709a,#fee140)',
        'linear-gradient(135deg,#30cfd0,#330867)',
        'linear-gradient(135deg,#a8edea,#fed6e3)',
        'linear-gradient(135deg,#ff9a9e,#fecfef)'
      ]
      return colors[(id || 0) % colors.length]
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/detail/detail?id=' + id })
    },
    goProfile() {
      uni.switchTab({ url: '/pages/profile/profile' })
    }
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding-bottom: 220rpx;
}

/* 头部 */
.head {
  background: linear-gradient(150deg, #4f46e5 0%, #6d4fe0 55%, #7c3aed 100%);
  padding: 0 32rpx;
  position: relative;
}
.head-statusbar {
  height: var(--status-bar-height, 0px);
}
.head-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0 24rpx;
}
.head-title {
  color: #fff;
  font-size: 44rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}
.head-sub {
  margin-top: 6rpx;
  color: rgba(255, 255, 255, 0.7);
  font-size: 24rpx;
  letter-spacing: 1rpx;
}
.head-avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  color: #fff;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 搜索框 */
.search {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 44rpx;
  height: 80rpx;
  padding: 0 12rpx 0 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(30, 20, 90, 0.15);
}
.search-icon {
  width: 26rpx;
  height: 26rpx;
  border: 4rpx solid #9ca3af;
  border-radius: 50%;
  position: relative;
  margin-right: 18rpx;
  flex-shrink: 0;
}
.search-icon::after {
  content: '';
  position: absolute;
  right: -10rpx;
  bottom: -8rpx;
  width: 4rpx;
  height: 14rpx;
  background: #9ca3af;
  transform: rotate(-45deg);
}
.search-input {
  flex: 1;
  height: 100%;
  font-size: 28rpx;
  color: #1f2937;
}
.ph {
  color: #b6bac6;
}
.search-clear {
  font-size: 40rpx;
  color: #c0c4cc;
  padding: 0 16rpx;
}
.search-btn {
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 30rpx;
  border-radius: 40rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 26rpx;
  flex-shrink: 0;
}

/* 分类 chips */
.cats {
  margin-top: 26rpx;
  white-space: nowrap;
  width: 100%;
}
.cat-chip {
  display: inline-block;
  padding: 0 28rpx;
  line-height: 60rpx;
  border-radius: 30rpx;
  margin-right: 18rpx;
  margin-bottom: 8rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.14);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}
.cat-chip.active {
  background: #fff;
  color: #4f46e5;
  font-weight: 600;
}
.head-bottom-radius {
  height: 28rpx;
  background: #f6f7fb;
  border-radius: 28rpx 28rpx 0 0;
  margin: 24rpx -32rpx 0;
}

/* 列表 */
.list-wrap {
  padding: 4rpx 24rpx 0;
}
.grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.book-card {
  width: 340rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.cover {
  position: relative;
  height: 340rpx;
  background: #eef0f6;
}
.cover-img {
  width: 100%;
  height: 100%;
}
.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-char {
  color: rgba(255, 255, 255, 0.85);
  font-size: 110rpx;
  font-weight: 700;
}
.cond-tag {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx 16rpx 16rpx 4rpx;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 20rpx;
  backdrop-filter: blur(4px);
}
.info {
  padding: 20rpx 22rpx 24rpx;
}
.title {
  font-size: 28rpx;
  color: #1f2937;
  font-weight: 600;
  line-height: 1.45;
  min-height: 80rpx;
}
.author {
  margin-top: 8rpx;
  font-size: 23rpx;
  color: #9ca3af;
}
.price-row {
  margin-top: 12rpx;
  display: flex;
  align-items: baseline;
}
.price {
  color: #ff5b2e;
  font-weight: 700;
}
.symbol {
  font-size: 24rpx;
}
.num {
  font-size: 36rpx;
}
.original {
  margin-left: 12rpx;
  font-size: 22rpx;
  color: #c0c4cc;
  text-decoration: line-through;
}

/* 空状态 */
.empty {
  padding: 120rpx 0;
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
  padding: 24rpx 0 40rpx;
  font-size: 24rpx;
  color: #b6bac6;
}
</style>
