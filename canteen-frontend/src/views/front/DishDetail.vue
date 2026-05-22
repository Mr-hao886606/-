<template>
  <div class="dish-detail-page">
    <div class="container" v-if="dish">
      <!-- 面包屑 -->
      <el-breadcrumb class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/dishes' }">菜品浏览</el-breadcrumb-item>
        <el-breadcrumb-item>{{ dish.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-card">
        <div class="detail-image">
          <img :src="dish.image || 'https://via.placeholder.com/400x300/fff5f5/e74c3c?text=🍽️'" :alt="dish.name" />
        </div>
        <div class="detail-info">
          <h1>{{ dish.name }}</h1>
          <div class="tags">
            <el-tag v-if="dish.categoryName">{{ dish.categoryName }}</el-tag>
            <el-tag v-if="dish.taste" type="warning">{{ dish.taste }}</el-tag>
            <el-tag v-if="dish.restaurantName" type="success">{{ dish.restaurantName }}</el-tag>
          </div>
          <div class="price-row">
            <span class="price">¥{{ dish.price }}</span>
            <span class="unit">/份</span>
          </div>
          <div class="stats">
            <span>销量: {{ dish.salesCount || 0 }}</span>
            <span>库存: {{ dish.stock || 0 }}</span>
            <span>点击: {{ dish.clickCount || 0 }}</span>
          </div>
          <div class="description" v-if="dish.description">
            <h4>菜品描述</h4>
            <p>{{ dish.description }}</p>
          </div>
          <div class="actions">
            <el-input-number v-model="quantity" :min="1" :max="dish.limitPerOrder || 10" size="large" />
            <el-button type="primary" size="large" @click="handleAddCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button size="large" :type="dish.isFavorite ? 'danger' : 'default'"
              @click="handleToggleFavorite">
              <el-icon><StarFilled v-if="dish.isFavorite" /><Star v-else /></el-icon>
              {{ dish.isFavorite ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 评价区 -->
      <div class="review-section">
        <h3>用户评价 ({{ reviews.length }})</h3>
        <div class="review-form" v-if="isLoggedIn">
          <el-rate v-model="reviewForm.rating" show-text />
          <el-input v-model="reviewForm.content" type="textarea" :rows="3" placeholder="写下您对这道菜的评价..." />
          <el-button type="primary" @click="submitReview" :loading="submitting">发表评价</el-button>
        </div>

        <div v-if="reviews.length === 0" class="no-review">
          <el-empty description="暂无评价，快来发表第一条评价吧" :image-size="80" />
        </div>

        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <el-avatar :size="36">{{ review.username?.charAt(0) || 'U' }}</el-avatar>
            <span class="review-user">{{ review.username }}</span>
            <el-rate :model-value="review.rating" disabled show-score text-color="#ff9900" size="small" />
            <span class="review-time">{{ review.createTime }}</span>
          </div>
          <p class="review-content">{{ review.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDishInfo, addToCart, toggleFavorite, getReviewList, addReview } from '@/api'

const route = useRoute()
const router = useRouter()

const dish = ref(null)
const quantity = ref(1)
const reviews = ref([])
const submitting = ref(false)

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const reviewForm = reactive({
  rating: 5,
  content: ''
})

const loadDish = async () => {
  try {
    const res = await getDishInfo(route.params.id)
    dish.value = res.data
  } catch (e) {
    router.push('/dishes')
  }
}

const loadReviews = async () => {
  try {
    const res = await getReviewList(route.params.id)
    reviews.value = res.data || []
  } catch (e) { /* ignore */ }
}

const handleAddCart = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart(dish.value.id, quantity.value)
    ElMessage.success('已添加到购物车')
  } catch (e) { /* handled */ }
}

const handleToggleFavorite = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleFavorite(dish.value.id)
    dish.value.isFavorite = !dish.value.isFavorite
    ElMessage.success(res.msg)
  } catch (e) { /* handled */ }
}

const submitReview = async () => {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submitting.value = true
  try {
    await addReview({
      dishId: dish.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    ElMessage.success('评价发表成功')
    reviewForm.content = ''
    reviewForm.rating = 5
    loadReviews()
  } catch (e) { /* handled */ }
  finally { submitting.value = false }
}

onMounted(() => {
  loadDish()
  loadReviews()
})
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px 20px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.detail-card {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.detail-image {
  width: 400px;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  flex: 1;
}

.detail-info h1 {
  font-size: 26px;
  margin-bottom: 12px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.price-row {
  margin-bottom: 12px;
}

.price {
  font-size: 36px;
  font-weight: 700;
  color: #e74c3c;
}

.unit {
  font-size: 14px;
  color: #999;
}

.stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
  margin-bottom: 20px;
}

.description {
  margin-bottom: 24px;
}

.description h4 {
  margin-bottom: 8px;
  font-size: 15px;
}

.description p {
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 评价区 */
.review-section {
  margin-top: 30px;
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.review-section h3 {
  margin-bottom: 20px;
  font-size: 18px;
}

.review-form {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.review-form .el-rate { margin-bottom: 12px; }
.review-form .el-textarea { margin-bottom: 12px; }

.no-review {
  padding: 30px 0;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.review-item:last-child { border-bottom: none; }

.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
  font-size: 14px;
}

.review-time {
  margin-left: auto;
  font-size: 12px;
  color: #999;
}

.review-content {
  margin-left: 46px;
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .detail-card { flex-direction: column; }
  .detail-image { width: 100%; height: 220px; }
}
</style>
