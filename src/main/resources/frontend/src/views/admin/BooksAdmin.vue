<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索书名 / 作者" clearable style="width: 220px" @keyup.enter="load" @clear="load" />
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="load">
        <el-option label="在售" value="ON_SALE" />
        <el-option label="已售" value="SOLD" />
        <el-option label="已下架" value="OFF_SHELF" />
      </el-select>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>
    <el-table :data="books" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="书名" min-width="160" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="sellerName" label="卖家" width="110" />
      <el-table-column prop="price" label="售价" width="90">
        <template #default="{ row }">￥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="conditionLevel" label="成色" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="110">
        <template #default="{ row }">{{ (row.createTime || '').slice(0, 10) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'OFF_SHELF'" link type="success" @click="toggle(row, 'ON_SALE')">上架</el-button>
          <el-button v-else link type="warning" @click="toggle(row, 'OFF_SHELF')">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="query.size"
        v-model:current-page="query.page" @current-change="load" />
    </div>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const books = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ keyword: '', status: '', page: 1, size: 10 })

const load = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/api/admin/books', query)
    books.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const statusText = (s) => ({ ON_SALE: '在售', SOLD: '已售', OFF_SHELF: '已下架' }[s] || s)
const statusType = (s) => ({ ON_SALE: 'success', SOLD: 'info', OFF_SHELF: 'warning' }[s] || 'info')

const toggle = async (row, status) => {
  await api.put(`/api/admin/books/${row.id}/status?status=${status}`)
  ElMessage.success(status === 'ON_SALE' ? '已上架' : '已下架')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 14px; }
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
</style>
