<template>
  <div class="coupons-page">
    <div class="container">
      <div class="page-header">
        <h2>我的优惠券</h2>
        <el-button type="primary" @click="showReceiveDialog = true">领取优惠券</el-button>
      </div>

      <div v-if="coupons.length > 0" class="coupon-list">
        <div v-for="c in coupons" :key="c.id" class="coupon-card">
          <div class="coupon-left">
            <span class="coupon-amount">¥{{ c.discountAmount }}</span>
            <span class="coupon-condition">满{{ c.minAmount }}元可用</span>
          </div>
          <div class="coupon-right">
            <h4>{{ c.name }}</h4>
            <p>{{ c.remark || '通用优惠券' }}</p>
            <p class="coupon-time">{{ c.startTime?.split(' ')[0] }} ~ {{ c.endTime?.split(' ')[0] }}</p>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无优惠券" />

      <!-- 领取弹窗 -->
      <el-dialog v-model="showReceiveDialog" title="可领取的优惠券" width="500px">
        <div v-if="allCoupons.length > 0">
          <div v-for="c in allCoupons" :key="c.id" class="receive-item">
            <div class="receive-info">
              <span class="c-amount">¥{{ c.discountAmount }}</span>
              <div>
                <h4>{{ c.name }}</h4>
                <p>满{{ c.minAmount }}元可用 | 剩余{{ c.remainCount }}张</p>
              </div>
            </div>
            <el-button type="primary" size="small" @click="receive(c.id)">领取</el-button>
          </div>
        </div>
        <el-empty v-else description="暂无可领取的优惠券" :image-size="80" />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCoupons, getCouponList, receiveCoupon } from '@/api'

const coupons = ref([])
const allCoupons = ref([])
const showReceiveDialog = ref(false)

const loadMyCoupons = async () => {
  const res = await getMyCoupons()
  coupons.value = res.data || []
}

const loadAllCoupons = async () => {
  const res = await getCouponList()
  allCoupons.value = res.data || []
}

const receive = async (id) => {
  try {
    await receiveCoupon(id)
    ElMessage.success('领取成功')
    loadMyCoupons()
    loadAllCoupons()
  } catch (e) { /* handled */ }
}

onMounted(() => {
  loadMyCoupons()
  loadAllCoupons()
})
</script>

<style scoped>
.container { max-width: 780px; margin: 0 auto; padding: 24px 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.coupon-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.coupon-card { display: flex; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.coupon-left { width: 120px; background: linear-gradient(135deg, #e74c3c, #c0392b); color: #fff; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px 10px; }
.coupon-amount { font-size: 28px; font-weight: 700; }
.coupon-condition { font-size: 12px; opacity: .8; margin-top: 4px; }
.coupon-right { flex: 1; padding: 16px; }
.coupon-right h4 { font-size: 15px; margin-bottom: 4px; }
.coupon-right p { font-size: 12px; color: #999; }
.coupon-time { margin-top: 8px; }
.receive-item { display: flex; justify-content: space-between; align-items: center; padding: 12px; border-bottom: 1px solid #f0f0f0; }
.receive-info { display: flex; align-items: center; gap: 16px; }
.c-amount { font-size: 20px; font-weight: 700; color: #e74c3c; }
</style>
