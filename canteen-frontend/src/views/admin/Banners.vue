<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>轮播图管理</h3>
      <el-button type="primary" @click="openDialog()">新增轮播图</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="160" />
      <el-table-column prop="image" label="图片" width="200">
        <template #default="{ row }">
          <img v-if="row.image" :src="row.image" style="height:50px;border-radius:4px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="form.image" placeholder="输入图片URL" /></el-form-item>
        <el-form-item label="链接"><el-input v-model="form.linkUrl" placeholder="可选" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { getBannerPage, saveBanner, deleteBanners } from '@/api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const form = reactive({ id: null, title: '', image: '', linkUrl: '', sortOrder: 0, status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBannerPage({ page: 1, limit: 100 })
    tableData.value = res.data?.records || []
  } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) { isEdit.value = true; Object.assign(form, row) }
  else { isEdit.value = false; Object.assign(form, { id: null, title: '', image: '', linkUrl: '', sortOrder: 0, status: 1 }) }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try { await saveBanner({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  finally { saving.value = false }
}

const handleDelete = async (id) => {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteBanners([id]); ElMessage.success('已删除'); loadData() }
  catch (e) { /* cancelled */ }
}

loadData()
</script>

<style scoped>
h3 { margin: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
