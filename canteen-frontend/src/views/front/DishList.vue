<template>
  <div class="dish-list-page">
    <div class="container">
      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <el-input v-model="searchKeyword" placeholder="搜索菜品名称..." class="search-input" clearable
          @keyup.enter="searchDishes" @clear="searchDishes">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>

        <el-select v-model="filterCategory" placeholder="美食分类" clearable @change="searchDishes" style="width: 140px">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>

        <el-select v-model="filterTaste" placeholder="口味偏好" clearable @change="searchDishes" style="width: 130px">
          <el-option label="辣" value="辣" />
          <el-option label="微辣" value="微辣" />
          <el-option label="不辣" value="不辣" />
          <el-option label="酸甜" value="酸甜" />
        </el-select>

        <el-select v-model="filterRestaurant" placeholder="选择餐厅" clearable @change="searchDishes" style="width: 140px">
          <el-option v-for="r in restaurants" :key="r.id" :label="r.name" :value="r.id" />
        </el-select>

        <el-button type="primary" @click="searchDishes">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
      </div>

      <!-- 菜品列表 -->
      <div class="dish-grid">
        <div v-for="dish in dishList" :key="dish.id" class="dish-card"
          @click="$router.push(`/dish/${dish.id}`)">
          <div class="dish-image">
            <img :src="dish.image || 'https://via.placeholder.com/280x200/fff5f5/e74c3c?text=🍽️'" :alt="dish.name" />
            <el-tag v-if="dish.taste" size="small" class="taste-tag" effect="dark">{{ dish.taste }}</el-tag>
          </div>
          <div class="dish-info">
            <h4>{{ dish.name }}</h4>
            <p class="dish-meta">
              <span class="cat-name">{{ dish.categoryName }}</span>
              <span class="rest-name">{{ dish.restaurantName }}</span>
            </p>
            <div class="dish-footer">
              <span class="price">¥{{ dish.price }}</span>
              <el-button type="primary" size="small" circle @click.stop="quickAdd(dish)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="dishList.length === 0 && !loading" description="暂无菜品" />

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="searchDishes"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDishList, getCategoryList, getRestaurantList, addToCart } from '@/api'

const route = useRoute()
const router = useRouter()

const dishList = ref([])
const categories = ref([])
const restaurants = ref([])
const loading = ref(false)
const total = ref(0)

const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const filterCategory = ref(null)
const filterTaste = ref(null)
const filterRestaurant = ref(null)

const searchDishes = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      keyword: searchKeyword.value || undefined,
      categoryId: filterCategory.value || undefined,
      taste: filterTaste.value || undefined,
      restaurantId: filterRestaurant.value || undefined
    }
    const res = await getDishList(params)
    dishList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const quickAdd = async (dish) => {
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart(dish.id, 1)
    ElMessage.success(`已添加「${dish.name}」到购物车`)
  } catch (e) { /* handled */ }
}

onMounted(async () => {
  try {
    const [catRes, restRes] = await Promise.all([
      getCategoryList(),
      getRestaurantList()
    ])
    categories.value = catRes.data || []
    restaurants.value = restRes.data || []
  } catch (e) { /* ignore */ }

  // 从URL参数获取预设筛选条件
  if (route.query.categoryId) filterCategory.value = parseInt(route.query.categoryId)
  if (route.query.keyword) searchKeyword.value = route.query.keyword
  searchDishes()
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  flex-wrap: wrap;
}

.search-input {
  width: 240px;
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
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.dish-image {
  position: relative;
  height: 200px;
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
  margin-bottom: 4px;
}

.dish-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
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

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .dish-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
