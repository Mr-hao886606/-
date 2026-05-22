<template>
  <div class="dashboard">
    <h2>📊 控制台</h2>
    <div class="stat-cards">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-icon" :style="{ background: s.color }">
          <el-icon :size="28"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDishPage, getOrderPage, getUserPage, getRestaurantPage } from '@/api'

const stats = ref([
  { label: '菜品总数', value: 0, icon: 'Food', color: '#e74c3c' },
  { label: '订单总数', value: 0, icon: 'Document', color: '#3498db' },
  { label: '用户总数', value: 0, icon: 'UserFilled', color: '#2ecc71' },
  { label: '餐厅数量', value: 0, icon: 'OfficeBuilding', color: '#f39c12' }
])

onMounted(async () => {
  try {
    const [dish, order, user, rest] = await Promise.all([
      getDishPage({ page: 1, limit: 1 }),
      getOrderPage({ page: 1, limit: 1 }),
      getUserPage({ page: 1, limit: 1 }),
      getRestaurantPage({ page: 1, limit: 1 })
    ])
    stats.value[0].value = dish.data?.total || 0
    stats.value[1].value = order.data?.total || 0
    stats.value[2].value = user.data?.total || 0
    stats.value[3].value = rest.data?.total || 0
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
h2 { margin-bottom: 24px; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.stat-card { background: #fff; border-radius: 12px; padding: 24px; display: flex; align-items: center; gap: 16px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.stat-icon { width: 60px; height: 60px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; }
.stat-value { font-size: 28px; font-weight: 700; display: block; }
.stat-label { font-size: 14px; color: #999; }
@media (max-width: 768px) { .stat-cards { grid-template-columns: repeat(2, 1fr); } }
</style>
