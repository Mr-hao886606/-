<template>
  <div class="address-page">
    <div class="container">
      <div class="page-header">
        <h2>收货地址</h2>
        <el-button type="primary" @click="openDialog()">新增地址</el-button>
      </div>

      <div v-if="addresses.length > 0" class="address-list">
        <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ default: addr.isDefault === 1 }">
          <div class="addr-header">
            <span class="addr-name">{{ addr.receiverName }}</span>
            <span class="addr-phone">{{ addr.receiverPhone }}</span>
            <el-tag v-if="addr.isDefault === 1" type="danger" size="small">默认</el-tag>
          </div>
          <p class="addr-detail">{{ addr.addressDetail }}</p>
          <div class="addr-actions">
            <el-button type="primary" link size="small" @click="openDialog(addr)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(addr.id)">删除</el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无收货地址" />

      <!-- 地址表单弹窗 -->
      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="480px">
        <el-form :model="form" label-width="80px">
          <el-form-item label="收货人">
            <el-input v-model="form.receiverName" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.receiverPhone" />
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="form.addressDetail" type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="设为默认">
            <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddressList, saveAddress, deleteAddress } from '@/api'

const addresses = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  addressDetail: '',
  isDefault: 0
})

const loadData = async () => {
  const res = await getAddressList()
  addresses.value = res.data || []
}

const openDialog = (addr) => {
  if (addr) {
    isEdit.value = true
    Object.assign(form, addr)
  } else {
    isEdit.value = false
    form.id = null
    form.receiverName = ''
    form.receiverPhone = ''
    form.addressDetail = ''
    form.isDefault = 0
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await saveAddress({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* handled */ }
  finally { saving.value = false }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('已删除')
    loadData()
  } catch (e) { /* cancelled or error */ }
}

onMounted(loadData)
</script>

<style scoped>
.container { max-width: 780px; margin: 0 auto; padding: 24px 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.address-card { background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.address-card.default { border: 2px solid #e74c3c; }
.addr-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.addr-name { font-size: 16px; font-weight: 600; }
.addr-phone { color: #666; font-size: 14px; }
.addr-detail { color: #666; font-size: 14px; margin-bottom: 8px; }
.addr-actions { display: flex; gap: 12px; }
</style>
