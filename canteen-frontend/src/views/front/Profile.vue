<template>
  <div class="profile-page">
    <div class="container">
      <h2>个人中心</h2>

      <el-tabs>
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息">
          <el-card class="info-card">
            <el-form :model="userForm" label-width="80px" style="max-width: 500px">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="userForm.realname" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码">
          <el-card class="info-card">
            <el-form :model="pwdForm" label-width="100px" style="max-width: 400px">
              <el-form-item label="原密码">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="pwdForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="changingPwd" @click="changePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUser, updatePassword } from '@/api'

const userForm = reactive({
  username: '',
  realname: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const saving = ref(false)
const changingPwd = ref(false)

onMounted(async () => {
  try {
    const res = await getUserInfo()
    Object.assign(userForm, res.data)
  } catch (e) { /* handled */ }
})

const saveInfo = async () => {
  saving.value = true
  try {
    await updateUser(userForm)
    ElMessage.success('信息更新成功')
    // 更新localStorage
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    Object.assign(userInfo, userForm)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  } catch (e) { /* handled */ }
  finally { saving.value = false }
}

const changePassword = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次新密码输入不一致')
    return
  }
  changingPwd.value = true
  try {
    await updatePassword(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (e) { /* handled */ }
  finally { changingPwd.value = false }
}
</script>

<style scoped>
.container {
  max-width: 700px;
  margin: 0 auto;
  padding: 24px 20px;
}

h2 { margin-bottom: 20px; }

.info-card {
  margin-top: 16px;
}
</style>
