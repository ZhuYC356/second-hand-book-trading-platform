<template>
  <div>
    <el-card class="filter" shadow="never">
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索书名 / 作者" clearable style="width: 240px" @keyup.enter="load" @clear="load" />
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px" @change="load">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="load">搜索</el-button>
      </div>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="book in books" :key="book.id" style="margin-bottom: 16px">
        <el-card shadow="hover" class="book-card" @click="showDetail(book.id)">
          <el-image :src="book.cover" fit="cover" class="cover">
            <template #error>
              <div class="cover-placeholder"><el-icon :size="36"><Reading /></el-icon></div>
            </template>
          </el-image>
          <div class="b-title">{{ book.title }}</div>
          <div class="b-author">{{ book.author || '佚名' }} · {{ book.categoryName }}</div>
          <div class="b-bottom">
            <span class="b-price">￥{{ book.price }}</span>
            <el-tag size="small" type="info">{{ book.conditionLevel }}</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-empty v-if="!loading && books.length === 0" description="暂无在售图书" style="width: 100%" />
    </el-row>

    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="query.size"
        v-model:current-page="query.page" @current-change="load" />
    </div>

    <el-dialog v-model="detailVisible" title="图书详情" width="560px">
      <div v-if="detail" class="detail">
        <el-image :src="detail.cover" fit="cover" class="detail-cover">
          <template #error><div class="cover-placeholder big"><el-icon :size="48"><Reading /></el-icon></div></template>
        </el-image>
        <div class="detail-info">
          <h3>{{ detail.title }}</h3>
          <p>作者：{{ detail.author || '佚名' }}</p>
          <p>分类：{{ detail.categoryName }}　成色：{{ detail.conditionLevel }}</p>
          <p>ISBN：{{ detail.isbn || '无' }}</p>
          <p class="d-price">￥{{ detail.price }}
            <span v-if="detail.originalPrice" class="d-origin">原价 ￥{{ detail.originalPrice }}</span>
          </p>
          <p>卖家：{{ detail.sellerName }}</p>
          <p class="d-desc">{{ detail.description }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!canBuy" :loading="buying" @click="onBuy">立即购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'
import { getUser } from '../../utils/auth'

const user = getUser()
const categories = ref([])
const books = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ keyword: '', categoryId: null, page: 1, size: 8 })

const detail = ref(null)
const detailVisible = ref(false)
const buying = ref(false)
const canBuy = ref(false)

const load = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/api/books/page', query)
    books.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const { data } = await api.get('/api/categories')
  categories.value = data
}

const showDetail = async (id) => {
  const { data } = await api.get(`/api/books/${id}`)
  detail.value = data
  canBuy.value = data.status === 'ON_SALE' && data.sellerId !== user.id
  detailVisible.value = true
}

const onBuy = async () => {
  buying.value = true
  try {
    await api.post('/api/orders/buy', { bookId: detail.value.id })
    ElMessage.success('下单成功，可在“我的订单”中付款')
    detailVisible.value = false
    load()
  } finally {
    buying.value = false
  }
}

onMounted(() => {
  loadCategories()
  load()
})
</script>

<style scoped>
.filter { margin-bottom: 16px; }
.filter-bar { display: flex; gap: 10px; align-items: center; }
.book-card { cursor: pointer; }
.cover { width: 100%; height: 180px; border-radius: 4px; background: #f0f2f5; display: block; }
.cover-placeholder {
  width: 100%; height: 180px; background: #f0f2f5; color: #c0c4cc;
  display: flex; align-items: center; justify-content: center;
}
.cover-placeholder.big { height: 200px; }
.b-title { margin-top: 10px; font-weight: bold; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.b-author { margin-top: 4px; font-size: 12px; color: #909399; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.b-bottom { margin-top: 8px; display: flex; justify-content: space-between; align-items: center; }
.b-price { color: #f56c6c; font-weight: bold; }
.pager { display: flex; justify-content: center; margin-top: 10px; }
.detail { display: flex; gap: 16px; }
.detail-cover { width: 180px; height: 240px; flex-shrink: 0; border-radius: 4px; background: #f0f2f5; }
.detail-info h3 { margin-bottom: 10px; }
.detail-info p { margin: 6px 0; color: #606266; font-size: 14px; }
.d-price { color: #f56c6c; font-size: 20px; font-weight: bold; }
.d-origin { color: #c0c4cc; font-size: 13px; text-decoration: line-through; font-weight: normal; margin-left: 6px; }
.d-desc { color: #909399; }
</style>
