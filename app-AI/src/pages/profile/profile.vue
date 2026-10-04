<template>
  <view class="profile-page">
    <app-header title="我的"></app-header>

    <!-- 用户信息卡 -->
    <view class="user-card">
      <view class="avatar">{{ avatarChar }}</view>
      <view class="user-info">
        <view class="nickname">{{ user.nickname || user.username || '未登录' }}</view>
        <view class="username">账号：{{ user.username || '-' }}</view>
      </view>
      <view class="role-badge">{{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}</view>
    </view>

    <!-- 统计 -->
    <view class="stats">
      <view class="stat" @tap="goOrders">
        <view class="stat-num">{{ sellCount }}</view>
        <view class="stat-label">我发布的</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat" @tap="goOrders">
        <view class="stat-num">{{ buyCount }}</view>
        <view class="stat-label">我买到的</view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat">
        <view class="stat-num">{{ pendingCount }}</view>
        <view class="stat-label">待付款</view>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="menu">
      <view class="menu-item" @tap="openProfileModal">
        <view class="menu-icon mi-profile"><wd-icon name="edit" size="32rpx" color="#4f46e5" /></view>
        <view class="menu-text">我的资料</view>
        <view class="menu-value ellipsis-1">{{ user.nickname || '-' }}</view>
        <view class="menu-arrow"></view>
      </view>
      <view class="menu-item" @tap="openPasswordModal">
        <view class="menu-icon mi-lock"><wd-icon name="lock-on" size="32rpx" color="#f59e0b" /></view>
        <view class="menu-text">修改密码</view>
        <view class="menu-value"></view>
        <view class="menu-arrow"></view>
      </view>
      <view class="menu-item" @tap="goPublish">
        <view class="menu-icon mi-book"><wd-icon name="books" size="32rpx" color="#10b981" /></view>
        <view class="menu-text">我的发布</view>
        <view class="menu-value"></view>
        <view class="menu-arrow"></view>
      </view>
      <view class="menu-item logout" @tap="logout">
        <view class="menu-icon mi-exit"><wd-icon name="poweroff" size="32rpx" color="#ef4444" /></view>
        <view class="menu-text">退出登录</view>
        <view class="menu-value"></view>
        <view class="menu-arrow"></view>
      </view>
    </view>

    <view class="version">书遇 · 二手书交易平台 v2.1</view>

    <!-- 我的资料弹窗 -->
    <view v-if="profileModal" class="modal-mask" @tap="profileModal = false">
      <view class="modal" @tap.stop>
        <view class="modal-title">我的资料</view>
        <view class="modal-field">
          <text class="modal-label">昵称</text>
          <input class="modal-input" v-model="profileForm.nickname" placeholder="请输入昵称" placeholder-class="ph" />
        </view>
        <view class="modal-field">
          <text class="modal-label">手机号</text>
          <input class="modal-input" v-model="profileForm.phone" type="number" placeholder="请输入手机号" placeholder-class="ph" />
        </view>
        <view class="modal-field">
          <text class="modal-label">邮箱</text>
          <input class="modal-input" v-model="profileForm.email" placeholder="请输入邮箱" placeholder-class="ph" />
        </view>
        <view class="modal-btns">
          <view class="modal-btn mb-cancel" @tap="profileModal = false">取消</view>
          <view class="modal-btn mb-ok" @tap="saveProfile">保存</view>
        </view>
      </view>
    </view>

    <!-- 修改密码弹窗 -->
    <view v-if="passwordModal" class="modal-mask" @tap="passwordModal = false">
      <view class="modal" @tap.stop>
        <view class="modal-title">修改密码</view>
        <view class="modal-field">
          <text class="modal-label">原密码</text>
          <input class="modal-input" v-model="passwordForm.oldPassword" password placeholder="请输入原密码" placeholder-class="ph" />
        </view>
        <view class="modal-field">
          <text class="modal-label">新密码</text>
          <input class="modal-input" v-model="passwordForm.newPassword" password placeholder="至少6位" placeholder-class="ph" />
        </view>
        <view class="modal-field">
          <text class="modal-label">确认新密码</text>
          <input class="modal-input" v-model="passwordForm.confirmPassword" password placeholder="再次输入新密码" placeholder-class="ph" />
        </view>
        <view class="modal-btns">
          <view class="modal-btn mb-cancel" @tap="passwordModal = false">取消</view>
          <view class="modal-btn mb-ok" @tap="savePassword">保存</view>
        </view>
      </view>
    </view>

    <app-tabbar current="profile" />
  </view>
</template>

<script>
import AppHeader from '@/components/app-header.vue'
import AppTabbar from '@/components/app-tabbar.vue'
import request from '@/utils/request'
import { toast } from '@/utils/util'

export default {
  components: { AppHeader, AppTabbar },
  data() {
    return {
      user: {},
      sellCount: 0,
      buyCount: 0,
      pendingCount: 0,
      profileModal: false,
      passwordModal: false,
      profileForm: { nickname: '', phone: '', email: '' },
      passwordForm: { oldPassword: '', newPassword: '', confirmPassword: '' }
    }
  },
  computed: {
    avatarChar() {
      return (this.user.nickname || this.user.username || '游').charAt(0)
    }
  },
  onShow() {
    this.loadUser()
    this.loadStats()
  },
  methods: {
    async loadUser() {
      try {
        this.user = (await request({ url: '/api/user/info', loading: false })) || {}
      } catch (e) {
        /* 401 已由 request 统一处理 */
      }
    },
    async loadStats() {
      try {
        const [myBooks, bought, pending] = await Promise.all([
          request({ url: '/api/books/my', data: { page: 1, size: 1 }, loading: false }),
          request({ url: '/api/orders/bought', data: { page: 1, size: 1 }, loading: false }),
          request({ url: '/api/orders/bought', data: { page: 1, size: 1, status: 'PENDING' }, loading: false })
        ])
        this.sellCount = myBooks.total || 0
        this.buyCount = bought.total || 0
        this.pendingCount = pending.total || 0
      } catch (e) {
        /* 统计失败静默 */
      }
    },
    openProfileModal() {
      this.profileForm = {
        nickname: this.user.nickname || '',
        phone: this.user.phone || '',
        email: this.user.email || ''
      }
      this.profileModal = true
    },
    async saveProfile() {
      if (!this.profileForm.nickname.trim()) return toast('请输入昵称')
      try {
        await request({
          url: '/api/user/profile',
          method: 'PUT',
          data: {
            nickname: this.profileForm.nickname.trim(),
            phone: this.profileForm.phone.trim(),
            email: this.profileForm.email.trim()
          }
        })
        toast('资料已更新', 'success')
        this.profileModal = false
        this.loadUser()
      } catch (e) {
        toast(e.message)
      }
    },
    openPasswordModal() {
      this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      this.passwordModal = true
    },
    async savePassword() {
      const { oldPassword, newPassword, confirmPassword } = this.passwordForm
      if (!oldPassword) return toast('请输入原密码')
      if (!newPassword || newPassword.length < 6) return toast('新密码至少6位')
      if (newPassword !== confirmPassword) return toast('两次输入的新密码不一致')
      try {
        await request({
          url: '/api/user/password',
          method: 'PUT',
          data: { oldPassword, newPassword }
        })
        toast('密码修改成功', 'success')
        this.passwordModal = false
      } catch (e) {
        toast(e.message)
      }
    },
    goOrders() {
      uni.switchTab({ url: '/pages/orders/orders' })
    },
    goPublish() {
      uni.switchTab({ url: '/pages/publish/publish' })
    },
    logout() {
      uni.showModal({
        title: '退出登录',
        content: '确定要退出当前账号吗？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            await request({ url: '/api/auth/logout', method: 'POST', loading: false })
          } catch (e) {
            /* 即使登出接口失败也继续清理本地 */
          }
          uni.removeStorageSync('APP_USER')
          uni.reLaunch({ url: '/pages/login/login' })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding-bottom: 220rpx;
}

/* 版本号 */
.version {
  margin-top: 40rpx;
  text-align: center;
  font-size: 22rpx;
  color: #c0c4cc;
  letter-spacing: 1rpx;
}

/* 用户卡 */
.user-card {
  display: flex;
  align-items: center;
  margin: -10rpx 24rpx 0;
  padding: 36rpx 32rpx;
  background: #fff;
  border-radius: 28rpx;
  box-shadow: 0 10rpx 30rpx rgba(31, 41, 55, 0.07);
  position: relative;
  z-index: 2;
}
.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 52rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(79, 70, 229, 0.35);
}
.user-info {
  flex: 1;
  margin-left: 28rpx;
  min-width: 0;
}
.nickname {
  font-size: 36rpx;
  font-weight: 700;
  color: #1f2937;
}
.username {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #9ca3af;
}
.role-badge {
  flex-shrink: 0;
  padding: 6rpx 20rpx;
  border-radius: 22rpx;
  background: #eef2ff;
  color: #4f46e5;
  font-size: 22rpx;
}

/* 统计 */
.stats {
  display: flex;
  align-items: center;
  margin: 24rpx 24rpx 0;
  padding: 30rpx 0;
  background: #fff;
  border-radius: 28rpx;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.stat {
  flex: 1;
  text-align: center;
}
.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
}
.stat-label {
  margin-top: 8rpx;
  font-size: 23rpx;
  color: #9ca3af;
}
.stat-divider {
  width: 1rpx;
  height: 56rpx;
  background: #f0f1f5;
}

/* 菜单 */
.menu {
  margin: 24rpx 24rpx 0;
  background: #fff;
  border-radius: 28rpx;
  overflow: hidden;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 32rpx;
  border-bottom: 1rpx solid #f0f1f5;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  margin-right: 24rpx;
  flex-shrink: 0;
}
.mi-profile {
  background: #eef2ff;
}
.mi-lock {
  background: #fff7ed;
}
.mi-book {
  background: #ecfdf5;
}
.mi-exit {
  background: #fef2f2;
}
.menu-text {
  font-size: 29rpx;
  color: #1f2937;
}
.menu-item.logout .menu-text {
  color: #ef4444;
}
.menu-value {
  flex: 1;
  text-align: right;
  font-size: 25rpx;
  color: #9ca3af;
  margin-left: 20rpx;
}
.menu-arrow {
  width: 16rpx;
  height: 16rpx;
  border-top: 4rpx solid #d1d5db;
  border-right: 4rpx solid #d1d5db;
  transform: rotate(45deg);
  margin-left: 16rpx;
  flex-shrink: 0;
}

/* 弹窗 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.55);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal {
  width: 600rpx;
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx 40rpx 32rpx;
}
.modal-title {
  text-align: center;
  font-size: 34rpx;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 32rpx;
}
.modal-field {
  margin-bottom: 26rpx;
}
.modal-label {
  display: block;
  font-size: 25rpx;
  color: #6b7280;
  margin-bottom: 12rpx;
}
.modal-input {
  height: 84rpx;
  background: #f6f7fb;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #1f2937;
}
.ph {
  color: #b6bac6;
}
.modal-btns {
  display: flex;
  margin-top: 36rpx;
}
.modal-btn {
  flex: 1;
  text-align: center;
  line-height: 88rpx;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 29rpx;
}
.mb-cancel {
  background: #f3f4f6;
  color: #6b7280;
  margin-right: 20rpx;
}
.mb-ok {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-weight: 600;
}
</style>
