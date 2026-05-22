<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>菜品管理</h3>
      <el-button type="primary" @click="openDialog()">新增菜品</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索菜品名称" clearable style="width: 220px" @keyup.enter="loadData" @clear="loadData" />
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="菜品名称" width="140" />
      <el-table-column prop="categoryName" label="分类" width="90" />
      <el-table-column prop="taste" label="口味" width="80" />
      <el-table-column prop="restaurantName" label="餐厅" width="120" />
      <el-table-column prop="price" label="价格" width="80" />
      <el-table-column prop="stock" label="库存" width="70" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" v-model:current-page="page" :total="total" :page-size="10"
      layout="total, prev, pager, next" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '新增菜品'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="菜品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="美食分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="口味">
          <el-select v-model="form.taste" placeholder="选择口味" clearable style="width: 100%">
            <el-option label="辣" value="辣" /><el-option label="微辣" value="微辣" />
            <el-option label="不辣" value="不辣" /><el-option label="酸甜" value="酸甜" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属餐厅">
          <el-select v-model="form.restaurantId" placeholder="选择餐厅" style="width: 100%">
            <el-option v-for="r in restaurants" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDishPage, saveDish, deleteDishes, getCategoryList, getRestaurantList } from '@/api'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const categories = ref([])
const restaurants = ref([])

const form = reactive({ id: null, name: '', categoryId: null, taste: '', restaurantId: null, price: 0, stock: 999, description: '', status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDishPage({ page: page.value, limit: 10, name: keyword.value || undefined })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) { isEdit.value = true; Object.assign(form, row) }
  else { isEdit.value = false; Object.assign(form, { id: null, name: '', categoryId: null, taste: '', restaurantId: null, price: 0, stock: 999, description: '', status: 1 }) }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try { await saveDish({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  finally { saving.value = false }
}

const handleDelete = async (id) => {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteDishes([id]); ElMessage.success('已删除'); loadData() }
  catch (e) { /* cancelled */ }
}

onMounted(async () => {
  loadData()
  const [c, r] = await Promise.all([getCategoryList(), getRestaurantList()])
  categories.value = c.data || []
  restaurants.value = r.data || []
})
</script>

<style scoped>
h3 { margin: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
