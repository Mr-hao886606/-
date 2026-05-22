<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>优惠券管理</h3>
      <el-button type="primary" @click="openDialog()">新增优惠券</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索优惠券名称" clearable style="width: 240px" @keyup.enter="loadData" @clear="loadData" />
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" width="160" />
      <el-table-column prop="minAmount" label="满额" width="90" />
      <el-table-column prop="discountAmount" label="优惠额" width="90" />
      <el-table-column prop="remainCount" label="剩余/总数" width="100">
        <template #default="{ row }">{{ row.remainCount || 0 }} / {{ row.totalCount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="startTime" label="生效时间" width="170" />
      <el-table-column prop="endTime" label="过期时间" width="170" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" v-model:current-page="page" :total="total" :page-size="10"
      layout="total, prev, pager, next" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑优惠券' : '新增优惠券'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="满额条件"><el-input-number v-model="form.minAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="优惠金额"><el-input-number v-model="form.discountAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="发放总数"><el-input-number v-model="form.totalCount" :min="1" /></el-form-item>
        <el-form-item label="剩余数量"><el-input-number v-model="form.remainCount" :min="0" /></el-form-item>
        <el-form-item label="生效时间">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCouponPage, saveCoupon, deleteCoupons } from '@/api'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const form = reactive({ id: null, name: '', minAmount: 0, discountAmount: 0, totalCount: 100, remainCount: 100, startTime: null, endTime: null, remark: '', status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCouponPage({ page: page.value, limit: 10, name: keyword.value || undefined })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) { isEdit.value = true; Object.assign(form, row) }
  else { isEdit.value = false; Object.assign(form, { id: null, name: '', minAmount: 0, discountAmount: 0, totalCount: 100, remainCount: 100, startTime: null, endTime: null, remark: '', status: 1 }) }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try { await saveCoupon({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  finally { saving.value = false }
}

const handleDelete = async (id) => {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteCoupons([id]); ElMessage.success('已删除'); loadData() }
  catch (e) { /* cancelled */ }
}

loadData()
</script>

<style scoped>
h3 { margin: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
