<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-inner">
        <div class="left">
          <span class="logo">📚</span>
          <span class="brand">二手书交易平台</span>
          <el-menu mode="horizontal" router :default-active="$route.path" :ellipsis="false" class="menu">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/my-books">我的发布</el-menu-item>
            <el-menu-item index="/my-orders">我的订单</el-menu-item>
          </el-menu>
        </div>
        <el-dropdown @command="onCommand">
          <span class="user">
            <el-icon><UserFilled /></el-icon>
            {{ user?.nickname || user?.username }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import api from '../api'
import { getUser, clearUser } from '../utils/auth'

const router = useRouter()
const user = getUser()

const onCommand = async (cmd) => {
  if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'logout') {
    await api.post('/api/auth/logout')
    clearUser()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { min-height: 100vh; }
.header { background: #fff; border-bottom: 1px solid #e4e7ed; padding: 0; }
.header-inner {
  max-width: 1100px; margin: 0 auto; height: 60px;
  display: flex; align-items: center; justify-content: space-between; padding: 0 16px;
}
.left { display: flex; align-items: center; }
.logo { font-size: 24px; margin-right: 8px; }
.brand { font-size: 18px; font-weight: bold; color: #303133; margin-right: 30px; }
.menu { border-bottom: none; }
.user { display: flex; align-items: center; gap: 4px; cursor: pointer; color: #303133; font-size: 14px; }
.main { max-width: 1100px; margin: 0 auto; width: 100%; padding: 20px 16px; }
</style>
