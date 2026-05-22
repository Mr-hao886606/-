<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <div class="banner-section">
      <el-carousel height="400px" :interval="4000" arrow="always">
        <el-carousel-item v-for="item in 3" :key="item">
          <div class="banner-item" :style="{ background: `linear-gradient(135deg, ${banners[item-1]})` }">
            <div class="banner-text">
              <h2>{{ ['美味快餐，即刻下单', '特色小吃，应有尽有', '健康饮食，从我做起'][item-1] }}</h2>
              <p>{{ ['精选食材，用心烹饪，让每一餐都充满幸福感', '各类地方特色美食，等您来品尝', '均衡营养搭配，为您的健康保驾护航'][item-1] }}</p>
              <el-button type="primary" size="large" round @click="$router.push('/dishes')">立即点餐</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类导航 -->
    <div class="section">
      <div class="container">
        <h3 class="section-title">美食分类</h3>
        <div class="category-grid">
          <div v-for="cat in categories" :key="cat.id" class="category-item"
            @click="$router.push({ path: '/dishes', query: { categoryId: cat.id } })">
            <div class="cat-icon">
              <el-icon :size="30"><Food /></el-icon>
            </div>
            <span>{{ cat.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门菜品 -->
    <div class="section hot-section">
      <div class="container">
        <div class="section-header">
          <h3 class="section-title">🔥 热销菜品</h3>
          <router-link to="/dishes" class="more-link">查看更多 →</router-link>
        </div>
        <div class="dish-grid">
          <div v-for="dish in hotDishes" :key="dish.id" class="dish-card"
            @click="$router.push(`/dish/${dish.id}`)">
            <div class="dish-image">
              <img :src="dish.image || 'https://via.placeholder.com/280x200/fff5f5/e74c3c?text=🍽️'" :alt="dish.name" />
              <el-tag v-if="dish.taste" size="small" class="taste-tag">{{ dish.taste }}</el-tag>
            </div>
            <div class="dish-info">
              <h4>{{ dish.name }}</h4>
              <p class="dish-desc">{{ dish.description || '美味可口，值得品尝' }}</p>
              <div class="dish-footer">
                <span class="price">¥{{ dish.price }}</span>
                <span class="sales">已售 {{ dish.salesCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新资讯 -->
    <div class="section">
      <div class="container">
        <div class="section-header">
          <h3 class="section-title">📢 美食资讯</h3>
          <router-link to="/news" class="more-link">更多 →</router-link>
        </div>
        <div class="news-list">
          <div v-for="item in newsList" :key="item.id" class="news-item"
            @click="$router.push(`/news/${item.id}`)">
            <span class="news-tag">{{ item.category }}</span>
            <span class="news-title">{{ item.title }}</span>
            <span class="news-time">{{ item.createTime?.split(' ')[0] }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotDishes, getCategoryList, getBannerList, getNewsList } from '@/api'

const hotDishes = ref([])
const categories = ref([])
const newsList = ref([])
const banners = ref(['#ff6b6b, #ee5a24', '#f0932b, #f5af19', '#e74c3c, #c0392b'])

onMounted(async () => {
  try {
    const [hotRes, catRes, newsRes] = await Promise.all([
      getHotDishes(),
      getCategoryList(),
      getNewsList({ page: 1, limit: 5 })
    ])
    hotDishes.value = hotRes.data || []
    categories.value = catRes.data || []
    newsList.value = newsRes.data?.records || []
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section {
  padding: 50px 0;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.more-link {
  color: #e74c3c;
  font-size: 14px;
}

.more-link:hover {
  text-decoration: underline;
}

/* 轮播图 */
.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-text {
  text-align: center;
  color: #fff;
}

.banner-text h2 {
  font-size: 42px;
  margin-bottom: 12px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
}

.banner-text p {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
}

/* 分类 */
.category-grid {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 28px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.category-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(231,76,60,0.15);
  color: #e74c3c;
}

.cat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #fff5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e74c3c;
}

/* 热门菜品 */
.hot-section {
  background: #fafafa;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.dish-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.dish-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
}

.dish-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.taste-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}

.dish-info {
  padding: 16px;
}

.dish-info h4 {
  font-size: 16px;
  margin-bottom: 6px;
}

.dish-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #e74c3c;
}

.sales {
  font-size: 12px;
  color: #999;
}

/* 资讯 */
.news-list {
  background: #fff;
  border-radius: 12px;
  padding: 16px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.news-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: color 0.3s;
}

.news-item:last-child {
  border-bottom: none;
}

.news-item:hover {
  color: #e74c3c;
}

.news-tag {
  font-size: 12px;
  background: #fff5f5;
  color: #e74c3c;
  padding: 2px 10px;
  border-radius: 4px;
  white-space: nowrap;
}

.news-title {
  flex: 1;
  font-size: 14px;
}

.news-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .dish-grid { grid-template-columns: repeat(2, 1fr); }
  .banner-text h2 { font-size: 28px; }
}
</style>
