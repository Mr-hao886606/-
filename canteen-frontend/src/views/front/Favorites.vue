<template>
  <div class="favorites-page">
    <div class="container">
      <h2>我的收藏</h2>
      <div v-if="dishes.length > 0" class="dish-grid">
        <div v-for="dish in dishes" :key="dish.id" class="dish-card"
          @click="$router.push(`/dish/${dish.id}`)">
          <div class="dish-image">
            <img :src="dish.image || 'https://via.placeholder.com/280x200/fff5f5/e74c3c?text=🍽️'" :alt="dish.name" />
          </div>
          <div class="dish-info">
            <h4>{{ dish.name }}</h4>
            <div class="dish-footer">
              <span class="price">¥{{ dish.price }}</span>
              <el-button type="danger" size="small" circle @click.stop="toggleFav(dish)">
                <el-icon><StarFilled /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无收藏">
        <el-button type="primary" @click="$router.push('/dishes')">去看看菜品</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavorites, toggleFavorite } from '@/api'

const dishes = ref([])

const loadData = async () => {
  try {
    const res = await getFavorites()
    dishes.value = res.data || []
  } catch (e) { /* handled */ }
}

const toggleFav = async (dish) => {
  try {
    await toggleFavorite(dish.id)
    dishes.value = dishes.value.filter(d => d.id !== dish.id)
    ElMessage.success('已取消收藏')
  } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 24px 20px; }
h2 { margin-bottom: 20px; }
.dish-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.dish-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all .3s; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.dish-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,.1); }
.dish-image { height: 180px; overflow: hidden; }
.dish-image img { width: 100%; height: 100%; object-fit: cover; }
.dish-info { padding: 16px; }
.dish-info h4 { font-size: 15px; margin-bottom: 8px; }
.dish-footer { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 18px; font-weight: 700; color: #e74c3c; }
@media (max-width: 768px) { .dish-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
