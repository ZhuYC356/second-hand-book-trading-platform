<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索图书名称" clearable style="width: 220px" @keyup.enter="load" @clear="load" />
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="load">
        <el-option label="待付款" value="PENDING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="bookTitle" label="图书" min-width="140" show-overflow-tooltip />
      <el-table-column prop="price" label="金额" width="90">
        <template #default="{ row }">￥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="buyerName" label="买家" width="110" />
      <el-table-column prop="sellerName" label="卖家" width="110" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170">
        <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ').slice(0, 16) }}</template>
      </el-table-column>
      <el-table-column prop="finishTime" label="完成时间" width="170">
        <template #default="{ row }">{{ row.finishTime ? row.finishTime.replace('T', ' ').slice(0, 16) : '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="remove(row)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const orders = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ keyword: '', status: '', page: 1, size: 10 })

const load = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/api/admin/orders', query)
    orders.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const statusText = (s) => ({ PENDING: '待付款', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusType = (s) => ({ PENDING: 'warning', COMPLETED: 'success', CANCELLED: 'info' }[s] || 'info')

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除订单 ${row.orderNo} 吗？`, '提示', { type: 'warning' })
  await api.del(`/api/admin/orders/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 14px; }
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
</style>
