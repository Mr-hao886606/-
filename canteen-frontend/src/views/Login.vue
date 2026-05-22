<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="40" color="#e74c3c"><Bowl /></el-icon>
        <h2>校园食堂</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password
            @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="user">我是用户</el-radio>
            <el-radio label="admin">我是管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <router-link to="/register">还没有账号？立即注册</router-link>
        <router-link to="/home">返回首页</router-link>
      </div>

      <div class="test-accounts">
        <p>测试账号：admin / admin123（管理员）</p>
        <p>测试账号：user1 / 123456（普通用户）</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getUserInfo } from '@/api'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: 'user1',
  password: '123456',
  role: 'user'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form.username, form.password, form.role)
    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))

    ElMessage.success('登录成功')

    // 跳转到目标页
    const redirect = route.query.redirect
    if (form.role === 'admin') {
      router.push('/admin')
    } else if (redirect) {
      router.push(redirect)
    } else {
      router.push('/home')
    }
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 50%, #f0932b 100%);
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  margin: 12px 0 4px;
  font-size: 24px;
  color: #333;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.login-footer a {
  color: #e74c3c;
}

.test-accounts {
  margin-top: 20px;
  padding: 12px;
  background: #fef9e7;
  border-radius: 8px;
  font-size: 12px;
  color: #b7950b;
}

.test-accounts p {
  margin: 2px 0;
}
</style>
