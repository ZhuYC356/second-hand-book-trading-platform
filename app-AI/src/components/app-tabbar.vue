<template>
  <view class="app-tabbar">
    <!-- 首页 -->
    <view class="tb-item" :class="{ active: current === 'home' }" @tap="go('home')">
      <view class="tb-icon"><wd-icon name="home" size="42rpx" /></view>
      <text class="tb-text">首页</text>
    </view>

    <!-- 发布：中间凸起主按钮 -->
    <view class="tb-item tb-item-pub" :class="{ active: current === 'publish' }" @tap="go('publish')">
      <view class="tb-pub"><wd-icon name="add-circle" size="46rpx" color="#ffffff" /></view>
      <text class="tb-text tb-text-pub">发布</text>
    </view>

    <!-- 订单 -->
    <view class="tb-item" :class="{ active: current === 'orders' }" @tap="go('orders')">
      <view class="tb-icon"><wd-icon name="list" size="42rpx" /></view>
      <text class="tb-text">订单</text>
    </view>

    <!-- 我的 -->
    <view class="tb-item" :class="{ active: current === 'profile' }" @tap="go('profile')">
      <view class="tb-icon"><wd-icon name="user" size="42rpx" /></view>
      <text class="tb-text">我的</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AppTabbar',
  props: {
    current: { type: String, default: 'home' }
  },
  methods: {
    go(key) {
      if (key === this.current) return
      const urls = {
        home: '/pages/home/home',
        publish: '/pages/publish/publish',
        orders: '/pages/orders/orders',
        profile: '/pages/profile/profile'
      }
      uni.switchTab({ url: urls[key] })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  align-items: flex-end;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(17, 24, 39, 0.05);
  box-shadow: 0 -6rpx 24rpx rgba(31, 41, 55, 0.06);
}

.tb-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 108rpx;
  color: #9ca3af;
}
.tb-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 46rpx;
  transition: transform 0.25s ease;
}
.tb-text {
  margin-top: 6rpx;
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1;
  transition: all 0.2s ease;
}
.tb-item.active {
  color: #4f46e5;
}
.tb-item.active .tb-icon {
  transform: translateY(-4rpx) scale(1.1);
}
.tb-item.active .tb-text {
  font-size: 26rpx;
  font-weight: 700;
}

/* 中间凸起发布按钮 */
.tb-item-pub {
  margin-top: -36rpx;
}
.tb-pub {
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 6rpx solid #ffffff;
  box-shadow: 0 10rpx 26rpx rgba(79, 70, 229, 0.42);
  transition: transform 0.25s ease;
}
.tb-item-pub:active .tb-pub {
  transform: scale(0.9);
}
.tb-text-pub {
  margin-top: 8rpx;
  color: #6b7280;
  font-weight: 600;
}
.tb-item-pub.active .tb-text-pub {
  color: #4f46e5;
  font-weight: 700;
}
</style>
