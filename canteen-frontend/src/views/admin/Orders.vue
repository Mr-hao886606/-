<template>
  <div class="admin-page">
    <h3>订单管理</h3>

    <div class="search-bar">
      <el-input v-model="orderNo" placeholder="搜索订单号" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData" />
      <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 140px" @change="loadData">
        <el-option label="待处理" value="pending" />
        <el-option label="已接单" value="accepted" />
        <el-option label="制作中" value="preparing" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="username" label="用户" width="100" />
      <el-table-column prop="totalPrice" label="总金额" width="90" />
      <el-table-column prop="actualPrice" label="实付" width="90" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-select v-model="row.status" size="small" @change="(val) => updateStatus(row, val)" style="width: 100px">
            <el-option label="待处理" value="pending" />
            <el-option label="已接单" value="accepted" />
            <el-option label="制作中" value="preparing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" v-model:current-page="page" :total="total" :page-size="10"
      layout="total, prev, pager, next" @current-change="loadData" />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="550px">
      <div v-if="currentOrder">
        <p><b>订单编号：</b>{{ currentOrder.orderNo }}</p>
        <p><b>用户：</b>{{ currentOrder.username }}</p>
        <p><b>状态：</b>{{ currentOrder.status }}</p>
        <p><b>备注：</b>{{ currentOrder.remark || '无' }}</p>
        <el-divider>菜品明细</el-divider>
        <div v-for="item in currentOrder.items" :key="item.id" class="detail-item">
          <span>{{ item.dishName }} × {{ item.quantity }}</span>
          <span style="color:#e74c3c;font-weight:600">¥{{ item.subtotal }}</span>
        </div>
        <el-divider />
        <p><b>总金额：</b>¥{{ currentOrder.totalPrice }}</p>
        <p v-if="currentOrder.discountAmount > 0"><b>优惠：</b>-¥{{ currentOrder.discountAmount }}</p>
        <p style="font-size:18px;color:#e74c3c"><b>实付：¥{{ currentOrder.actualPrice }}</b></p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, updateOrderStatus, deleteOrders } from '@/api'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const orderNo = ref('')
const statusFilter = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({ page: page.value, limit: 10, orderNo: orderNo.value || undefined, status: statusFilter.value || undefined })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const updateStatus = async (row, status) => {
  try { await updateOrderStatus(row.id, status); ElMessage.success('状态已更新') }
  catch (e) { loadData() }
}

const showDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleDelete = async (id) => {
  try { await ElMessageBox.confirm('确定删除该订单？', '提示', { type: 'warning' }); await deleteOrders([id]); ElMessage.success('已删除'); loadData() }
  catch (e) { /* cancelled */ }
}

loadData()
</script>

<style scoped>
h3 { margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
.detail-item { display: flex; justify-content: space-between; padding: 6px 0; }
</style>
