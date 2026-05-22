<template>
  <div class="front-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-inner">
        <router-link to="/home" class="logo">
          <el-icon :size="28" color="#e74c3c"><Bowl /></el-icon>
          <span class="logo-text">校园食堂</span>
        </router-link>

        <nav class="nav">
          <router-link to="/home" active-class="active">首页</router-link>
          <router-link to="/dishes" active-class="active">菜品浏览</router-link>
          <router-link to="/news" active-class="active">美食资讯</router-link>
        </nav>

        <div class="header-actions">
          <template v-if="isLoggedIn">
            <router-link to="/cart" class="cart-btn">
              <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
                <el-icon :size="22"><ShoppingCart /></el-icon>
              </el-badge>
            </router-link>
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar">{{ userInfo.realname?.charAt(0) || 'U' }}</el-avatar>
                <span class="username">{{ userInfo.realname || userInfo.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="coupons">优惠券</el-dropdown-item>
                  <el-dropdown-item v-if="userInfo.role === 'admin'" command="admin" divided>后台管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="login-link">登录</router-link>
            <router-link to="/register" class="register-link">
              <el-button type="primary" size="small">注册</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-inner">
        <p>© 2024 校园食堂点餐平台 - 让每一餐更简单</p>
        <p>校园美食，尽在指尖</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCartCount } from '@/api'

const router = useRouter()
const route = useRoute()

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const userInfo = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}'))
const cartCount = ref(0)

const loadCartCount = async () => {
  if (isLoggedIn.value) {
    try {
      const res = await getCartCount()
      cartCount.value = res.data || 0
    } catch { /* ignore */ }
  }
}

const handleUserCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    cartCount.value = 0
    router.push('/home')
  } else if (command === 'admin') {
    router.push('/admin')
  } else {
    router.push(`/${command}`)
  }
}

onMounted(loadCartCount)
watch(route, loadCartCount)
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 40px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav a {
  font-size: 15px;
  color: #666;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav a:hover,
.nav a.active {
  color: #e74c3c;
  border-bottom-color: #e74c3c;
}

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-btn {
  color: #666;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.3s;
}

.cart-btn:hover {
  color: #e74c3c;
  background: #fef0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

.login-link {
  color: #666;
  font-size: 14px;
}

.login-link:hover {
  color: #e74c3c;
}

.main-content {
  flex: 1;
}

.footer {
  background: #2c3e50;
  color: #bdc3c7;
  text-align: center;
  padding: 24px 0;
  margin-top: 40px;
}

.footer p {
  margin: 4px 0;
  font-size: 13px;
}
</style>
