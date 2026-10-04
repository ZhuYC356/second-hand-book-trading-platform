<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <span>我发布的图书</span>
        <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>添加图书</el-button>
      </div>
      <el-table :data="books" v-loading="loading" stripe>
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image :src="row.cover" fit="cover" style="width: 44px; height: 56px; border-radius: 4px; background: #f0f2f5">
              <template #error><div class="mini-cover"><el-icon><Reading /></el-icon></div></template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="售价" width="80">
          <template #default="{ row }">￥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="conditionLevel" label="成色" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="110">
          <template #default="{ row }">{{ (row.createTime || '').slice(0, 10) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'OFF_SHELF'" link type="success" @click="toggle(row, 'ON_SALE')">上架</el-button>
            <el-button v-else-if="row.status === 'ON_SALE'" link type="warning" @click="toggle(row, 'OFF_SHELF')">下架</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager">
        <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="query.size"
          v-model:current-page="query.page" @current-change="load" />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" :title="form.id ? '编辑图书' : '添加图书'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="书名" required>
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" placeholder="选填" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成色">
          <el-select v-model="form.conditionLevel" style="width: 100%">
            <el-option v-for="c in conditions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="onUploadSuccess" :on-error="() => ElMessage.error('上传失败')" accept="image/*">
            <el-image v-if="form.cover" :src="form.cover" fit="cover" style="width: 70px; height: 92px; border-radius: 4px" />
            <el-button v-else type="primary" plain>上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="图书的详细描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const conditions = ['全新', '九成新', '八成新', '七成新及以下']
const categories = ref([])
const books = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 8 })
const formVisible = ref(false)
const saving = ref(false)
const form = reactive({})

const load = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/api/books/my', query)
    books.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const statusText = (s) => ({ ON_SALE: '在售', SOLD: '已售', OFF_SHELF: '已下架' }[s] || s)
const statusType = (s) => ({ ON_SALE: 'success', SOLD: 'info', OFF_SHELF: 'warning' }[s] || 'info')

const openAdd = () => {
  Object.assign(form, {
    id: null, title: '', author: '', isbn: '', categoryId: null,
    price: null, originalPrice: null, conditionLevel: '九成新', description: '', cover: ''
  })
  formVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  formVisible.value = true
}

const onUploadSuccess = (res) => {
  if (res.code === 0) {
    form.cover = res.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(res.msg)
  }
}

const save = async () => {
  if (!form.title || !form.title.trim()) {
    ElMessage.warning('请输入书名')
    return
  }
  if (form.price == null) {
    ElMessage.warning('请输入售价')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await api.put('/api/books', form)
    } else {
      await api.post('/api/books', form)
    }
    ElMessage.success('保存成功')
    formVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const toggle = async (row, status) => {
  await api.put(`/api/books/${row.id}/status?status=${status}`)
  ElMessage.success(status === 'ON_SALE' ? '已上架' : '已下架')
  load()
}

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除《${row.title}》吗？`, '提示', { type: 'warning' })
  await api.del(`/api/books/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  const { data } = await api.get('/api/categories')
  categories.value = data
  load()
})
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-weight: bold; }
.pager { display: flex; justify-content: flex-end; margin-top: 14px; }
.mini-cover {
  width: 44px; height: 56px; background: #f0f2f5; color: #c0c4cc; border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
}
</style>
