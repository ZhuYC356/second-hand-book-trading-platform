<template>
  <el-card shadow="never">
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane label="我买到的" name="bought" />
      <el-tab-pane label="我卖出的" name="sold" />
    </el-tabs>
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="bookTitle" label="图书" min-width="140" show-overflow-tooltip />
      <el-table-column prop="price" label="金额" width="90">
        <template #default="{ row }">￥{{ row.price }}</template>
      </el-table-column>
      <el-table-column :label="tab === 'bought' ? '卖家' : '买家'" width="110">
        <template #default="{ row }">{{ tab === 'bought' ? row.sellerName : row.buyerName }}</template>
      </el-table-column>
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
      <el-table-column v-if="tab === 'bought'" label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button link type="success" @click="pay(row)">付款</el-button>
            <el-button link type="danger" @click="cancel(row)">取消</el-button>
          </template>
          <span v-else style="color: #c0c4cc; font-size: 12px">-</span>
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

const tab = ref('bought')
const orders = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10 })

const load = async () => {
  loading.value = true
  try {
    const url = tab.value === 'bought' ? '/api/orders/bought' : '/api/orders/sold'
    const { data } = await api.get(url, query)
    orders.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const statusText = (s) => ({ PENDING: '待付款', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusType = (s) => ({ PENDING: 'warning', COMPLETED: 'success', CANCELLED: 'info' }[s] || 'info')

const pay = async (row) => {
  await ElMessageBox.confirm(`确认支付 ￥${row.price} 购买《${row.bookTitle}》吗？`, '付款确认', { type: 'info' })
  await api.put(`/api/orders/${row.id}/pay`)
  ElMessage.success('付款成功，交易完成')
  load()
}

const cancel = async (row) => {
  await ElMessageBox.confirm(`确定取消购买《${row.bookTitle}》吗？`, '提示', { type: 'warning' })
  await api.put(`/api/orders/${row.id}/cancel`)
  ElMessage.success('订单已取消')
  load()
}

onMounted(load)
</script>

<style scoped>
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
</style>
