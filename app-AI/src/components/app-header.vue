<template>
  <view class="app-header" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="app-header__bar">
      <view v-if="back" class="app-header__back" @tap="goBack">
        <view class="arrow"></view>
      </view>
      <view class="app-header__title" :style="{ textAlign: back ? 'center' : 'left' }">
        {{ title }}
      </view>
      <view class="app-header__right">
        <slot name="right"></slot>
      </view>
    </view>
    <slot></slot>
  </view>
</template>

<script>
export default {
  name: 'AppHeader',
  props: {
    title: { type: String, default: '' },
    back: { type: Boolean, default: false },
    /** 渐变背景是否开启，默认开启 */
    gradient: { type: Boolean, default: true }
  },
  data() {
    return {
      statusBarHeight: 0
    }
  },
  mounted() {
    // 小程序/App 下需要避开状态栏，H5 为 0
    try {
      const info = uni.getSystemInfoSync()
      this.statusBarHeight = info.statusBarHeight || 0
    } catch (e) {
      this.statusBarHeight = 0
    }
  },
  methods: {
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
.app-header {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  padding-bottom: 24rpx;
  position: relative;
  z-index: 10;
}
.app-header__bar {
  height: 88rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  position: relative;
}
.app-header__back {
  width: 64rpx;
  height: 64rpx;
  margin-left: -16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}
.arrow {
  width: 20rpx;
  height: 20rpx;
  border-left: 5rpx solid #fff;
  border-bottom: 5rpx solid #fff;
  transform: rotate(45deg);
  margin-left: 8rpx;
}
.app-header__title {
  flex: 1;
  color: #fff;
  font-size: 34rpx;
  font-weight: 600;
}
.app-header__right {
  position: absolute;
  right: 32rpx;
  top: 0;
  height: 88rpx;
  display: flex;
  align-items: center;
}
</style>
