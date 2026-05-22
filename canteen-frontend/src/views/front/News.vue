<template>
  <div class="news-page">
    <div class="container">
      <h2>美食资讯</h2>
      <div v-if="newsList.length > 0" class="news-grid">
        <div v-for="item in newsList" :key="item.id" class="news-card"
          @click="$router.push(`/news/${item.id}`)">
          <div class="news-cover">
            <img :src="item.coverImage || 'https://via.placeholder.com/360x200/f0f0f0/999?text=📰'" :alt="item.title" />
            <el-tag class="news-tag" size="small">{{ item.category }}</el-tag>
          </div>
          <div class="news-body">
            <h4>{{ item.title }}</h4>
            <p class="news-desc" v-if="item.content">{{ item.content.replace(/<[^>]+>/g, '').substring(0, 80) }}...</p>
            <div class="news-meta">
              <span>{{ item.createTime }}</span>
              <span>{{ item.clickCount || 0 }} 阅读</span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无资讯" />
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination v-model:current-page="page" :total="total" :page-size="10"
          layout="prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewsList } from '@/api'

const newsList = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await getNewsList({ page: page.value, limit: 10 })
  newsList.value = res.data?.records || []
  total.value = res.data?.total || 0
}

onMounted(loadData)
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 24px 20px; }
h2 { margin-bottom: 20px; }
.news-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.news-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all .3s; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.news-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,.1); }
.news-cover { position: relative; height: 180px; overflow: hidden; }
.news-cover img { width: 100%; height: 100%; object-fit: cover; }
.news-tag { position: absolute; top: 8px; left: 8px; }
.news-body { padding: 16px; }
.news-body h4 { font-size: 15px; margin-bottom: 8px; }
.news-desc { font-size: 13px; color: #999; margin-bottom: 8px; }
.news-meta { display: flex; justify-content: space-between; font-size: 12px; color: #bbb; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
</style>
