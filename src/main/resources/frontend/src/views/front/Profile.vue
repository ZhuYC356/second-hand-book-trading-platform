<template>
  <el-row :gutter="16">
    <el-col :span="12">
      <el-card shadow="never">
        <template #header><b>基本资料</b></template>
        <el-form :model="profile" label-width="80px">
          <el-form-item label="账号">
            <el-input v-model="profile.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="profile.nickname" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profile.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.email" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card shadow="never">
        <template #header><b>修改密码</b></template>
        <el-form :model="pwd" label-width="80px">
          <el-form-item label="原密码">
            <el-input v-model="pwd.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwd.newPassword" type="password" show-password placeholder="至少6位" />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="pwd.confirm" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingPwd" @click="savePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'
import { getUser, setUser } from '../../utils/auth'

const profile = reactive({ username: '', nickname: '', phone: '', email: '' })
const savingProfile = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '', confirm: '' })
const savingPwd = ref(false)

onMounted(async () => {
  const { data } = await api.get('/api/user/info')
  Object.assign(profile, data)
})

const saveProfile = async () => {
  savingProfile.value = true
  try {
    await api.put('/api/user/profile', { nickname: profile.nickname, phone: profile.phone, email: profile.email })
    const user = getUser()
    if (user) {
      user.nickname = profile.nickname
      setUser(user)
    }
    ElMessage.success('保存成功')
  } finally {
    savingProfile.value = false
  }
}

const savePwd = async () => {
  if (!pwd.oldPassword || !pwd.newPassword) {
    ElMessage.warning('请输入原密码和新密码')
    return
  }
  if (pwd.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }
  if (pwd.newPassword !== pwd.confirm) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  savingPwd.value = true
  try {
    await api.put('/api/user/password', { oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success('密码修改成功')
    pwd.oldPassword = ''
    pwd.newPassword = ''
    pwd.confirm = ''
  } finally {
    savingPwd.value = false
  }
}
</script>
