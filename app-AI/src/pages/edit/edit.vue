<template>
  <view class="edit-page">
    <view class="form-card">
      <!-- 封面 -->
      <view class="form-item">
        <view class="form-label">封面图片</view>
        <view class="cover-row">
          <view v-if="form.cover" class="cover-preview">
            <image :src="imgUrl(form.cover)" mode="aspectFill" class="cover-img" />
            <view class="cover-del" @tap="form.cover = ''">×</view>
          </view>
          <view v-else class="cover-add" @tap="chooseCover">
            <view class="plus">+</view>
            <view class="plus-text">上传封面</view>
          </view>
          <view class="cover-tip">建议正方形图片，不超过10MB</view>
        </view>
      </view>

      <!-- 书名 -->
      <view class="form-item">
        <view class="form-label">书名 <text class="req">*</text></view>
        <input class="form-input" v-model="form.title" placeholder="请输入书名" placeholder-class="ph" />
      </view>

      <!-- 分类 -->
      <view class="form-item">
        <view class="form-label">分类 <text class="req">*</text></view>
        <picker mode="selector" :range="catNames" @change="onCatChange">
          <view class="form-picker" :class="{ placeholder: !form.categoryId }">
            {{ catName || '请选择分类' }}
            <view class="chevron"></view>
          </view>
        </picker>
      </view>

      <!-- 作者 / ISBN -->
      <view class="form-item">
        <view class="form-label">作者</view>
        <input class="form-input" v-model="form.author" placeholder="请输入作者" placeholder-class="ph" />
      </view>
      <view class="form-item">
        <view class="form-label">ISBN</view>
        <input class="form-input" v-model="form.isbn" placeholder="选填" placeholder-class="ph" />
      </view>

      <!-- 价格 -->
      <view class="form-row">
        <view class="form-item half">
          <view class="form-label">售价（元）<text class="req">*</text></view>
          <input class="form-input" v-model="form.price" type="digit" placeholder="0.00" placeholder-class="ph" />
        </view>
        <view class="form-item half">
          <view class="form-label">原价（元）</view>
          <input class="form-input" v-model="form.originalPrice" type="digit" placeholder="选填" placeholder-class="ph" />
        </view>
      </view>

      <!-- 成色 -->
      <view class="form-item">
        <view class="form-label">成色 <text class="req">*</text></view>
        <view class="cond-list">
          <view
            v-for="c in conditions"
            :key="c"
            class="cond-chip"
            :class="{ active: form.conditionLevel === c }"
            @tap="form.conditionLevel = c"
          >{{ c }}</view>
        </view>
      </view>

      <!-- 描述 -->
      <view class="form-item">
        <view class="form-label">宝贝描述</view>
        <textarea
          class="form-textarea"
          v-model="form.description"
          placeholder="描述一下书籍的品相、有无笔记划线、交易方式等"
          placeholder-class="ph"
          :maxlength="500"
        />
        <view class="count">{{ (form.description || '').length }}/500</view>
      </view>
    </view>

    <view class="btn-wrap">
      <button class="save-btn" :disabled="saving" @tap="save">{{ saving ? '保存中...' : (isEdit ? '保存修改' : '发布图书') }}</button>
    </view>
  </view>
</template>

<script>
import request, { uploadFile } from '@/utils/request'
import { imgUrl, toast } from '@/utils/util'

const CONDITIONS = ['全新', '九成新', '八成新', '七成新', '六成新及以下']

export default {
  data() {
    return {
      id: 0,
      isEdit: false,
      saving: false,
      conditions: CONDITIONS,
      catList: [],
      form: {
        title: '',
        categoryId: 0,
        author: '',
        isbn: '',
        price: '',
        originalPrice: '',
        conditionLevel: '八成新',
        description: '',
        cover: ''
      }
    }
  },
  computed: {
    catNames() {
      return this.catList.map((c) => c.name)
    },
    catName() {
      const cat = this.catList.find((c) => c.id === this.form.categoryId)
      return cat ? cat.name : ''
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = Number(options.id)
      this.isEdit = true
      uni.setNavigationBarTitle({ title: '编辑图书' })
      this.loadBook()
    }
    this.loadCats()
  },
  methods: {
    imgUrl,
    async loadCats() {
      try {
        this.catList = (await request({ url: '/api/categories', loading: false })) || []
      } catch (e) {
        toast(e.message)
      }
    },
    async loadBook() {
      try {
        const book = await request({ url: '/api/books/' + this.id })
        this.form = {
          title: book.title || '',
          categoryId: book.categoryId || 0,
          author: book.author || '',
          isbn: book.isbn || '',
          price: book.price != null ? String(book.price) : '',
          originalPrice: book.originalPrice != null ? String(book.originalPrice) : '',
          conditionLevel: book.conditionLevel || '八成新',
          description: book.description || '',
          cover: book.cover || ''
        }
      } catch (e) {
        toast(e.message)
      }
    },
    onCatChange(e) {
      const idx = Number(e.detail.value)
      this.form.categoryId = this.catList[idx] ? this.catList[idx].id : 0
    },
    chooseCover() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        success: async (res) => {
          const path = res.tempFilePaths && res.tempFilePaths[0]
          if (!path) return
          try {
            const url = await uploadFile(path)
            this.form.cover = url
            toast('封面上传成功', 'success')
          } catch (e) {
            toast(e.message)
          }
        }
      })
    },
    validate() {
      if (!this.form.title.trim()) return '请输入书名'
      if (!this.form.categoryId) return '请选择分类'
      const price = Number(this.form.price)
      if (isNaN(price) || price < 0 || this.form.price === '') return '请输入正确的售价'
      if (this.form.originalPrice !== '' && (isNaN(Number(this.form.originalPrice)) || Number(this.form.originalPrice) < 0)) {
        return '请输入正确的原价'
      }
      if (!this.form.conditionLevel) return '请选择成色'
      return ''
    },
    async save() {
      const msg = this.validate()
      if (msg) return toast(msg)
      this.saving = true
      try {
        const data = {
          categoryId: this.form.categoryId,
          title: this.form.title.trim(),
          author: this.form.author.trim(),
          isbn: this.form.isbn.trim(),
          price: Number(this.form.price),
          originalPrice: this.form.originalPrice === '' ? null : Number(this.form.originalPrice),
          conditionLevel: this.form.conditionLevel,
          description: this.form.description.trim(),
          cover: this.form.cover || null
        }
        if (this.isEdit) {
          data.id = this.id
          await request({ url: '/api/books', method: 'PUT', data })
          toast('保存成功', 'success')
        } else {
          await request({ url: '/api/books', method: 'POST', data })
          toast('发布成功', 'success')
        }
        setTimeout(() => uni.navigateBack(), 700)
      } catch (e) {
        toast(e.message)
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.edit-page {
  min-height: 100vh;
  background: #f6f7fb;
  padding: 24rpx 24rpx 60rpx;
}
.form-card {
  background: #fff;
  border-radius: 28rpx;
  padding: 16rpx 32rpx;
  box-shadow: 0 6rpx 20rpx rgba(31, 41, 55, 0.06);
}
.form-item {
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f0f1f5;
}
.form-item:last-child {
  border-bottom: none;
}
.form-row {
  display: flex;
  border-bottom: 1rpx solid #f0f1f5;
}
.form-row .form-item {
  border-bottom: none;
}
.half {
  flex: 1;
}
.form-label {
  font-size: 26rpx;
  color: #4b5563;
  margin-bottom: 16rpx;
}
.req {
  color: #ef4444;
  margin-left: 4rpx;
}
.form-input {
  height: 88rpx;
  background: #f6f7fb;
  border-radius: 18rpx;
  padding: 0 26rpx;
  font-size: 29rpx;
  color: #1f2937;
}
.form-picker {
  height: 88rpx;
  line-height: 88rpx;
  background: #f6f7fb;
  border-radius: 18rpx;
  padding: 0 26rpx;
  font-size: 29rpx;
  color: #1f2937;
  position: relative;
}
.form-picker.placeholder {
  color: #b6bac6;
}
.chevron {
  position: absolute;
  right: 28rpx;
  top: 34rpx;
  width: 16rpx;
  height: 16rpx;
  border-right: 4rpx solid #c0c4cc;
  border-bottom: 4rpx solid #c0c4cc;
  transform: rotate(45deg);
}
.ph {
  color: #b6bac6;
}

/* 封面 */
.cover-row {
  display: flex;
  align-items: center;
}
.cover-preview {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 18rpx;
  overflow: hidden;
}
.cover-img {
  width: 100%;
  height: 100%;
}
.cover-del {
  position: absolute;
  top: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 34rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 0 0 18rpx;
}
.cover-add {
  width: 160rpx;
  height: 160rpx;
  border-radius: 18rpx;
  border: 2rpx dashed #c7cbdd;
  background: #f8f9fc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.plus {
  font-size: 56rpx;
  color: #b6bac6;
  line-height: 1;
}
.plus-text {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #9ca3af;
}
.cover-tip {
  margin-left: 24rpx;
  font-size: 22rpx;
  color: #b6bac6;
  line-height: 1.6;
}

/* 成色 */
.cond-list {
  display: flex;
  flex-wrap: wrap;
}
.cond-chip {
  padding: 0 28rpx;
  line-height: 64rpx;
  border-radius: 32rpx;
  background: #f6f7fb;
  color: #6b7280;
  font-size: 25rpx;
  margin: 0 16rpx 16rpx 0;
  border: 1rpx solid #eef0f6;
}
.cond-chip.active {
  background: #eef2ff;
  color: #4f46e5;
  border-color: #c7d2fe;
  font-weight: 600;
}

/* 描述 */
.form-textarea {
  width: 100%;
  height: 220rpx;
  background: #f6f7fb;
  border-radius: 18rpx;
  padding: 22rpx 26rpx;
  font-size: 28rpx;
  color: #1f2937;
  box-sizing: border-box;
}
.count {
  text-align: right;
  font-size: 22rpx;
  color: #c0c4cc;
  margin-top: 8rpx;
}

.btn-wrap {
  margin-top: 40rpx;
}
.save-btn {
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  box-shadow: 0 12rpx 28rpx rgba(79, 70, 229, 0.35);
}
.save-btn[disabled] {
  opacity: 0.6;
}
</style>
