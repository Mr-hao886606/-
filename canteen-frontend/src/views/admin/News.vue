<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>资讯管理</h3>
      <el-button type="primary" @click="openDialog()">新增资讯</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索标题" clearable style="width: 260px" @keyup.enter="loadData" @clear="loadData" />
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="clickCount" label="阅读量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" v-model:current-page="page" :total="total" :page-size="10"
      layout="total, prev, pager, next" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑资讯' : '新增资讯'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 200px">
            <el-option label="通知" value="通知" /><el-option label="活动" value="活动" /><el-option label="健康贴士" value="健康贴士" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="支持HTML内容" />
        </el-form-item>
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
import { getNewsPage, saveNews, deleteNews } from '@/api'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const form = reactive({ id: null, title: '', category: '通知', content: '', status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNewsPage({ page: page.value, limit: 10, title: keyword.value || undefined })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) { isEdit.value = true; Object.assign(form, row) }
  else { isEdit.value = false; Object.assign(form, { id: null, title: '', category: '通知', content: '', status: 1 }) }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try { await saveNews({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  finally { saving.value = false }
}

const handleDelete = async (id) => {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteNews([id]); ElMessage.success('已删除'); loadData() }
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
