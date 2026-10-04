<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">二手书交易平台</h2>
      <el-form label-width="0" @keyup.enter="onLogin">
        <el-form-item>
          <el-radio-group v-model="form.role" style="width: 100%">
            <el-radio-button value="USER" style="width: 50%">前台用户</el-radio-button>
            <el-radio-button value="ADMIN" style="width: 50%">后台管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onLogin">登 录</el-button>
        <div v-if="form.role === 'USER'" class="register-link">
          还没有账号？<el-link type="primary" @click="registerVisible = true">立即注册</el-link>
        </div>
        <div class="tip">管理员账号：admin / 123456，用户账号：user1 / 123456</div>
      </el-form>
    </el-card>

    <el-dialog v-model="registerVisible" title="用户注册" width="400px">
      <el-form label-width="70px">
        <el-form-item label="账号">
          <el-input v-model="reg.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="reg.nickname" placeholder="展示昵称" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="reg.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="regLoading" @click="onRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { setUser } from '../utils/auth'

const router = useRouter()
const form = reactive({ role: 'USER', username: '', password: '' })
const loading = ref(false)

const onLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const { data } = await api.post('/api/auth/login', form)
    setUser(data)
    ElMessage.success('登录成功')
    router.push(data.role === 'ADMIN' ? '/admin' : '/')
  } finally {
    loading.value = false
  }
}

const registerVisible = ref(false)
const regLoading = ref(false)
const reg = reactive({ username: '', nickname: '', password: '' })

const onRegister = async () => {
  if (!reg.username || !reg.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  if (reg.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  regLoading.value = true
  try {
    await api.post('/api/auth/register', reg)
    ElMessage.success('注册成功，请登录')
    form.username = reg.username
    form.password = ''
    registerVisible.value = false
    reg.username = ''
    reg.nickname = ''
    reg.password = ''
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card { width: 380px; padding: 10px 10px 0; }
.title { text-align: center; margin-bottom: 24px; color: #303133; }
.register-link { text-align: center; margin-top: 14px; font-size: 14px; color: #909399; }
.tip { margin-top: 16px; font-size: 12px; color: #909399; text-align: center; }
</style>
