<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="brand">📚 二手书后台</div>
      <el-menu router :default-active="$route.path" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin"><el-icon><DataAnalysis /></el-icon>数据统计</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="/admin/books"><el-icon><Reading /></el-icon>图书管理</el-menu-item>
        <el-menu-item index="/admin/orders"><el-icon><List /></el-icon>订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-title">{{ $route.meta?.title || '后台管理' }}</div>
        <el-dropdown @command="onCommand">
          <span class="user"><el-icon><UserFilled /></el-icon>{{ user?.nickname || user?.username }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import api from '../api'
import { getUser, clearUser } from '../utils/auth'

const router = useRouter()
const user = getUser()

const onCommand = async (cmd) => {
  if (cmd === 'logout') {
    await api.post('/api/auth/logout')
    clearUser()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { min-height: 100vh; }
.aside { background: #304156; }
.brand { height: 60px; line-height: 60px; text-align: center; color: #fff; font-weight: bold; font-size: 16px; }
.aside :deep(.el-menu) { border-right: none; }
.header { background: #fff; border-bottom: 1px solid #e4e7ed; display: flex; align-items: center; justify-content: space-between; }
.header-title { font-size: 16px; font-weight: bold; color: #303133; }
.user { display: flex; align-items: center; gap: 4px; cursor: pointer; color: #303133; font-size: 14px; }
.main { background: #f5f6f8; }
</style>
