<template>
  <div class="news-detail-page">
    <div class="container" v-if="news">
      <el-breadcrumb class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/news' }">美食资讯</el-breadcrumb-item>
        <el-breadcrumb-item>{{ news.title }}</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="detail-card">
        <h1>{{ news.title }}</h1>
        <div class="meta">
          <span>{{ news.category }}</span>
          <span>{{ news.createTime }}</span>
          <span>{{ news.clickCount || 0 }} 次阅读</span>
        </div>
        <div class="content" v-html="news.content || '暂无详细内容'"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNewsInfo } from '@/api'

const route = useRoute()
const news = ref(null)

onMounted(async () => {
  const res = await getNewsInfo(route.params.id)
  news.value = res.data
})
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 24px 20px; }
.breadcrumb { margin-bottom: 20px; }
.detail-card { background: #fff; border-radius: 16px; padding: 30px; box-shadow: 0 2px 12px rgba(0,0,0,.06); }
h1 { font-size: 24px; margin-bottom: 16px; }
.meta { display: flex; gap: 16px; font-size: 13px; color: #999; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; }
.content { line-height: 1.8; font-size: 15px; color: #444; }
</style>
