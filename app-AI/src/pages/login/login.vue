<template>
  <view class="login-page">
    <!-- 装饰背景 -->
    <view class="bg">
      <view class="blob blob-1"></view>
      <view class="blob blob-2"></view>
      <view class="blob blob-3"></view>
    </view>

    <!-- 品牌区 -->
    <view class="brand">
      <view class="brand-logo">
        <view class="book-icon">
          <view class="book-cover"></view>
          <view class="book-page"></view>
        </view>
      </view>
      <view class="brand-name">书遇</view>
      <view class="brand-slogan">好书值得被再次阅读 · 二手书交易平台</view>
    </view>

    <!-- 表单卡片 -->
    <view class="card">
      <view class="seg">
        <view class="seg-item" :class="{ active: mode === 'login' }" @tap="mode = 'login'">登录</view>
        <view class="seg-item" :class="{ active: mode === 'register' }" @tap="mode = 'register'">注册</view>
        <view class="seg-thumb" :class="{ right: mode === 'register' }"></view>
      </view>

      <!-- 登录表单 -->
      <view v-if="mode === 'login'" class="form">
        <view class="field">
          <text class="field-label">用户名</text>
          <input class="field-input" v-model="loginForm.username" placeholder="请输入用户名" placeholder-class="ph" />
        </view>
        <view class="field">
          <text class="field-label">密码</text>
          <input class="field-input" v-model="loginForm.password" password placeholder="请输入密码" placeholder-class="ph" />
        </view>
        <button class="submit-btn" :disabled="submitting" @tap="doLogin">
          {{ submitting ? '登录中...' : '登 录' }}
        </button>
        <view class="tips">演示账号：user1 ~ user6　密码：123456</view>
      </view>

      <!-- 注册表单 -->
      <view v-else class="form">
        <view class="field">
          <text class="field-label">用户名</text>
          <input class="field-input" v-model="regForm.username" placeholder="3-20位字母或数字" placeholder-class="ph" />
        </view>
        <view class="field">
          <text class="field-label">昵称</text>
          <input class="field-input" v-model="regForm.nickname" placeholder="展示给别人看的名字" placeholder-class="ph" />
        </view>
        <view class="field">
          <text class="field-label">密码</text>
          <input class="field-input" v-model="regForm.password" password placeholder="至少6位" placeholder-class="ph" />
        </view>
        <view class="field">
          <text class="field-label">确认密码</text>
          <input class="field-input" v-model="regForm.confirmPassword" password placeholder="再次输入密码" placeholder-class="ph" />
        </view>
        <button class="submit-btn" :disabled="submitting" @tap="doRegister">
          {{ submitting ? '注册中...' : '注 册' }}
        </button>
      </view>
    </view>

    <view class="footer">登录即代表同意《用户协议》与《隐私政策》</view>
  </view>
</template>

<script>
import request from '@/utils/request'
import { toast } from '@/utils/util'

export default {
  data() {
    return {
      mode: 'login',
      submitting: false,
      loginForm: { username: '', password: '' },
      regForm: { username: '', nickname: '', password: '', confirmPassword: '' }
    }
  },
  methods: {
    async doLogin() {
      const { username, password } = this.loginForm
      if (!username.trim()) return toast('请输入用户名')
      if (!password) return toast('请输入密码')
      this.submitting = true
      try {
        const user = await request({
          url: '/api/auth/login',
          method: 'POST',
          data: { username: username.trim(), password, role: 'USER' }
        })
        uni.setStorageSync('APP_USER', user)
        toast('登录成功，欢迎回来', 'success')
        setTimeout(() => {
          uni.switchTab({ url: '/pages/home/home' })
        }, 600)
      } catch (e) {
        toast(e.message)
      } finally {
        this.submitting = false
      }
    },
    async doRegister() {
      const { username, nickname, password, confirmPassword } = this.regForm
      if (!username.trim()) return toast('请输入用户名')
      if (!nickname.trim()) return toast('请输入昵称')
      if (!password || password.length < 6) return toast('密码至少6位')
      if (password !== confirmPassword) return toast('两次输入的密码不一致')
      this.submitting = true
      try {
        await request({
          url: '/api/auth/register',
          method: 'POST',
          data: { username: username.trim(), nickname: nickname.trim(), password }
        })
        toast('注册成功，请登录', 'success')
        this.loginForm.username = username.trim()
        setTimeout(() => {
          this.mode = 'login'
        }, 600)
      } catch (e) {
        toast(e.message)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(150deg, #4f46e5 0%, #6d4fe0 45%, #7c3aed 100%);
  position: relative;
  overflow: hidden;
  padding: 0 48rpx;
}

/* 装饰气泡 */
.bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.blob {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
}
.blob-1 {
  width: 460rpx;
  height: 460rpx;
  top: -140rpx;
  right: -120rpx;
}
.blob-2 {
  width: 260rpx;
  height: 260rpx;
  top: 300rpx;
  left: -100rpx;
  background: rgba(255, 255, 255, 0.06);
}
.blob-3 {
  width: 160rpx;
  height: 160rpx;
  top: 200rpx;
  right: 120rpx;
  background: rgba(255, 255, 255, 0.1);
}

/* 品牌区 */
.brand {
  padding-top: 140rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.brand-logo {
  width: 132rpx;
  height: 132rpx;
  border-radius: 36rpx;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
}
/* 纯 CSS 画一本书 */
.book-icon {
  position: relative;
  width: 64rpx;
  height: 76rpx;
}
.book-cover {
  position: absolute;
  left: 0;
  top: 0;
  width: 48rpx;
  height: 72rpx;
  background: #fff;
  border-radius: 4rpx 10rpx 10rpx 4rpx;
  box-shadow: 6rpx 4rpx 0 rgba(124, 58, 237, 0.35);
}
.book-page {
  position: absolute;
  left: 56rpx;
  top: 6rpx;
  width: 8rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4rpx;
}
.brand-name {
  color: #fff;
  font-size: 52rpx;
  font-weight: 700;
  letter-spacing: 6rpx;
}
.brand-slogan {
  margin-top: 14rpx;
  color: rgba(255, 255, 255, 0.75);
  font-size: 24rpx;
  letter-spacing: 2rpx;
}

/* 表单卡片 */
.card {
  margin-top: 72rpx;
  background: #fff;
  border-radius: 40rpx;
  padding: 44rpx 44rpx 40rpx;
  box-shadow: 0 24rpx 60rpx rgba(30, 20, 90, 0.25);
}
.seg {
  position: relative;
  display: flex;
  background: #f2f3f9;
  border-radius: 20rpx;
  padding: 6rpx;
  margin-bottom: 40rpx;
}
.seg-item {
  position: relative;
  z-index: 2;
  flex: 1;
  text-align: center;
  line-height: 76rpx;
  font-size: 30rpx;
  color: #6b7280;
  transition: color 0.25s;
}
.seg-item.active {
  color: #4f46e5;
  font-weight: 600;
}
.seg-thumb {
  position: absolute;
  z-index: 1;
  left: 6rpx;
  top: 6rpx;
  width: calc(50% - 6rpx);
  height: 76rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(79, 70, 229, 0.12);
  transition: transform 0.25s ease;
}
.seg-thumb.right {
  transform: translateX(100%);
}

.field {
  margin-bottom: 32rpx;
}
.field-label {
  display: block;
  font-size: 26rpx;
  color: #6b7280;
  margin-bottom: 14rpx;
}
.field-input {
  height: 92rpx;
  background: #f6f7fb;
  border-radius: 20rpx;
  padding: 0 28rpx;
  font-size: 30rpx;
  color: #1f2937;
}
.ph {
  color: #b6bac6;
}

.submit-btn {
  margin-top: 16rpx;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 4rpx;
  box-shadow: 0 12rpx 28rpx rgba(79, 70, 229, 0.35);
}
.submit-btn[disabled] {
  opacity: 0.6;
}
.tips {
  margin-top: 28rpx;
  text-align: center;
  font-size: 24rpx;
  color: #9ca3af;
}

.footer {
  margin: 44rpx 0 60rpx;
  text-align: center;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.55);
}
</style>
