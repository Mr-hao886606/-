<template>
  <div class="orders-page">
    <div class="container">
      <h2>我的订单</h2>

      <el-tabs v-model="activeTab" @tab-change="loadOrders">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="已接单" name="accepted" />
        <el-tab-pane label="制作中" name="preparing" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <div v-if="orders.length > 0">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span>订单编号：{{ order.orderNo }}</span>
            <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
            <span class="order-time">{{ order.createTime }}</span>
          </div>

          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.dishImage || 'https://via.placeholder.com/60/fff5f5/e74c3c?text=🍽️'" class="item-img" />
              <span class="item-name">{{ item.dishName }}</span>
              <span class="item-qty">×{{ item.quantity }}</span>
              <span class="item-price">¥{{ item.subtotal }}</span>
            </div>
          </div>

          <div class="order-footer">
            <span v-if="order.discountAmount > 0" class="discount-info">
              优惠：-¥{{ order.discountAmount }}
            </span>
            <span class="actual-price">
              实付：<b>¥{{ order.actualPrice }}</b>
            </span>
            <el-button v-if="order.status === 'pending'" type="danger" size="small" plain
              @click="cancelOrder(order)">
              取消订单
            </el-button>
          </div>
        </div>

        <div class="pagination-wrap">
          <el-pagination v-model:current-page="page" :total="total" :page-size="10"
            layout="prev, pager, next" @current-change="loadOrders" />
        </div>
      </div>

      <el-empty v-else description="暂无订单" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrders, cancelOrder as cancelOrderApi } from '@/api'

const orders = ref([])
const activeTab = ref('')
const page = ref(1)
const total = ref(0)

const statusMap = {
  pending: '待处理',
  accepted: '已接单',
  preparing: '制作中',
  completed: '已完成',
  cancelled: '已取消'
}

const statusType = (s) => {
  const map = { pending: 'warning', accepted: 'primary', preparing: '', completed: 'success', cancelled: 'info' }
  return map[s] || 'info'
}

const statusText = (s) => statusMap[s] || s

const loadOrders = async () => {
  try {
    const res = await getMyOrders({ page: page.value, limit: 10, status: activeTab.value || undefined })
    orders.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await cancelOrderApi(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) { /* cancelled or error */ }
}

onMounted(loadOrders)
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px;
}

h2 { margin-bottom: 20px; }

.order-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}

.order-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: #fafafa;
  font-size: 13px;
}

.order-time { margin-left: auto; color: #999; }

.order-items { padding: 12px 20px; }

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.item-img {
  width: 50px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
}

.item-name { flex: 1; font-size: 14px; }
.item-qty { color: #999; font-size: 13px; }
.item-price { font-weight: 600; color: #e74c3c; }

.order-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  padding: 12px 20px;
  border-top: 1px solid #f5f5f5;
}

.discount-info { color: #67c23a; font-size: 13px; }

.actual-price { font-size: 15px; }
.actual-price b { color: #e74c3c; font-size: 18px; }

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
