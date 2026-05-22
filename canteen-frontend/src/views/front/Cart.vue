<template>
  <div class="cart-page">
    <div class="container">
      <h2>我的购物车</h2>

      <div v-if="cartList.length > 0">
        <div class="cart-list">
          <div v-for="item in cartList" :key="item.id" class="cart-item">
            <div class="item-image">
              <img :src="item.dishImage || 'https://via.placeholder.com/100x80/fff5f5/e74c3c?text=🍽️'" :alt="item.dishName" />
            </div>
            <div class="item-info">
              <h4>{{ item.dishName }}</h4>
              <span class="item-price">¥{{ item.price }}</span>
            </div>
            <div class="item-qty">
              <el-input-number v-model="item.quantity" :min="1" :max="99" size="small"
                @change="(val) => updateQty(item, val)" />
            </div>
            <div class="item-subtotal">
              ¥{{ (item.price * item.quantity).toFixed(2) }}
            </div>
            <div class="item-action">
              <el-button type="danger" size="small" circle @click="removeItem(item)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="cart-footer">
          <div class="total-info">
            <span>已选 <b>{{ cartList.length }}</b> 件商品，合计：</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="showOrderDialog = true">
            去结算
          </el-button>
        </div>
      </div>

      <el-empty v-else description="购物车空空如也">
        <el-button type="primary" @click="$router.push('/dishes')">去逛逛</el-button>
      </el-empty>
    </div>

    <!-- 下单弹窗 -->
    <el-dialog v-model="showOrderDialog" title="确认下单" width="500px">
      <div class="order-summary">
        <div v-for="item in cartList" :key="item.id" class="order-item">
          <span>{{ item.dishName }} × {{ item.quantity }}</span>
          <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
        <el-divider />
        <div class="order-total">
          合计：<span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <el-form label-width="80px" style="margin-top: 16px">
        <el-form-item label="收货地址">
          <el-select v-model="selectedAddressId" placeholder="请选择收货地址" style="width: 100%">
            <el-option v-for="addr in addresses" :key="addr.id"
              :label="`${addr.receiverName} - ${addr.addressDetail}`"
              :value="addr.id" />
          </el-select>
          <el-button type="primary" link size="small" @click="$router.push('/address')">管理地址</el-button>
        </el-form-item>
        <el-form-item label="优惠券">
          <el-select v-model="selectedCouponId" placeholder="选择优惠券（可选）" clearable style="width: 100%">
            <el-option v-for="c in myCoupons" :key="c.id"
              :label="`${c.name} (满${c.minAmount}减${c.discountAmount})`"
              :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="remark" type="textarea" :rows="2" placeholder="如有特殊要求请备注" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showOrderDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">
          确认下单（¥{{ totalPrice.toFixed(2) }}）
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartQty, removeFromCart, createOrder, getAddressList, getMyCoupons } from '@/api'

const router = useRouter()
const cartList = ref([])
const addresses = ref([])
const myCoupons = ref([])
const showOrderDialog = ref(false)
const selectedAddressId = ref(null)
const selectedCouponId = ref(null)
const remark = ref('')
const submitting = ref(false)

const totalPrice = computed(() => {
  return cartList.value.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0)
})

const loadCart = async () => {
  try {
    const res = await getCartList()
    cartList.value = res.data || []
  } catch (e) { /* handled */ }
}

const updateQty = async (item, val) => {
  try {
    await updateCartQty(item.id, val)
  } catch (e) { loadCart() }
}

const removeItem = async (item) => {
  try {
    await ElMessageBox.confirm('确定移除该商品？', '提示', { type: 'warning' })
    await removeFromCart(item.id)
    cartList.value = cartList.value.filter(c => c.id !== item.id)
    ElMessage.success('已移除')
  } catch (e) { /* cancelled or error */ }
}

const loadOrderData = async () => {
  try {
    const [addrRes, couponRes] = await Promise.all([
      getAddressList(),
      getMyCoupons()
    ])
    addresses.value = addrRes.data || []
    myCoupons.value = couponRes.data || []
    if (addresses.value.length > 0) {
      const defaultAddr = addresses.value.find(a => a.isDefault === 1)
      selectedAddressId.value = defaultAddr ? defaultAddr.id : addresses.value[0].id
    }
  } catch (e) { /* ignore */ }
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    await createOrder({
      addressId: selectedAddressId.value,
      couponId: selectedCouponId.value || null,
      remark: remark.value
    })
    ElMessage.success('下单成功！')
    showOrderDialog.value = false
    router.push('/orders')
  } catch (e) { /* handled */ }
  finally { submitting.value = false }
}

onMounted(() => {
  loadCart()
  loadOrderData()
})
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px;
}

h2 {
  margin-bottom: 20px;
}

.cart-list {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.cart-item:last-child { border-bottom: none; }

.item-image {
  width: 100px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info { flex: 1; }
.item-info h4 { font-size: 15px; margin-bottom: 4px; }
.item-price { font-size: 14px; color: #e74c3c; font-weight: 600; }

.item-subtotal {
  font-size: 16px;
  font-weight: 600;
  color: #e74c3c;
  min-width: 80px;
  text-align: center;
}

.cart-footer {
  margin-top: 20px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.total-info { font-size: 15px; }

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #e74c3c;
  margin-left: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.order-total {
  text-align: right;
  font-size: 16px;
}

.order-total .total-price {
  font-size: 22px;
  font-weight: 700;
  color: #e74c3c;
}
</style>
