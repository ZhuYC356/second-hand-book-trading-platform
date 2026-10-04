<template>
  <div v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="never" class="stat-card">
          <div class="stat">
            <div class="stat-icon" :style="{ background: card.color }"><el-icon :size="24"><component :is="card.icon" /></el-icon></div>
            <div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><b>近7日订单量趋势</b></template>
          <div ref="orderTrendRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><b>近7日销售额（已完成订单）</b></template>
          <div ref="amountTrendRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><b>图书分类数量占比</b></template>
          <div ref="categoryRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><b>热门书籍 TOP5（按成交单数）</b></template>
          <div ref="topBooksRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import api from '../../api'

const loading = ref(true)
const summary = ref(null)
const orderTrendRef = ref()
const amountTrendRef = ref()
const categoryRef = ref()
const topBooksRef = ref()
const charts = []
const cards = ref([
  { label: '用户总数', value: 0, icon: 'User', color: '#409EFF' },
  { label: '图书总数', value: 0, icon: 'Reading', color: '#67C23A' },
  { label: '订单总数', value: 0, icon: 'List', color: '#E6A23C' },
  { label: '成交总额', value: 0, icon: 'Coin', color: '#F56C6C' }
])

const makeChart = (el) => {
  const chart = echarts.init(el)
  charts.push(chart)
  return chart
}

const renderCharts = async () => {
  const [trend, catDist, topBooks] = await Promise.all([
    api.get('/api/admin/stats/order-trend'),
    api.get('/api/admin/stats/category-dist'),
    api.get('/api/admin/stats/top-books')
  ])
  const dates = trend.data.map((t) => t.date.slice(5))
  const counts = trend.data.map((t) => t.count)
  const amounts = trend.data.map((t) => Number(t.amount))

  makeChart(orderTrendRef.value).setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '订单量', type: 'line', data: counts, smooth: true, areaStyle: { opacity: 0.15 }, itemStyle: { color: '#409EFF' } }]
  })

  makeChart(amountTrendRef.value).setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: [{ name: '销售额', type: 'bar', data: amounts, itemStyle: { color: '#67C23A', borderRadius: [4, 4, 0, 0] } }]
  })

  makeChart(categoryRef.value).setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', right: 10, top: 'center', type: 'scroll' },
    series: [{
      name: '分类占比', type: 'pie', radius: ['40%', '65%'], center: ['40%', '50%'],
      data: catDist.data,
      label: { show: false }
    }]
  })

  const top = [...topBooks.data].reverse()
  makeChart(topBooksRef.value).setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 140, right: 40, top: 20, bottom: 30 },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: top.map((t) => t.title), axisLabel: { width: 120, overflow: 'truncate' } },
    series: [{ type: 'bar', data: top.map((t) => t.sales), itemStyle: { color: '#E6A23C', borderRadius: [0, 4, 4, 0] }, barWidth: 18 }]
  })
}

const loadSummary = async () => {
  const { data } = await api.get('/api/admin/stats/summary')
  summary.value = data
  cards.value[0].value = data.userCount
  cards.value[1].value = data.bookCount
  cards.value[2].value = data.orderCount
  cards.value[3].value = '￥' + Number(data.totalAmount).toFixed(2)
}

const resize = () => charts.forEach((c) => c.resize())

onMounted(async () => {
  try {
    await Promise.all([loadSummary(), renderCharts()])
  } finally {
    loading.value = false
  }
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.stat-card { margin-bottom: 0; }
.stat { display: flex; align-items: center; gap: 14px; }
.stat-icon {
  width: 48px; height: 48px; border-radius: 10px; color: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-value { font-size: 22px; font-weight: bold; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 2px; }
.chart { height: 300px; }
</style>
