<script setup>
import { computed, onMounted, ref } from 'vue'
import { usePostStore } from '@/stores/posts'

const postStore = usePostStore()
const activeFilter = ref('all')
const selectedOrder = ref(null)
const message = ref('')

const filters = [
  { label: '全部订单', value: 'all' },
  { label: '待付款', value: 'pending' },
  { label: '已支付', value: 'paid' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') return postStore.orders
  return postStore.orders.filter((order) => order.status === activeFilter.value)
})

async function handleOrder(order, action) {
  message.value = ''
  try {
    if (action === 'pay') await postStore.payOrderAction(order.id)
    if (action === 'cancel') {
      if (!window.confirm('确定取消订单吗？')) return
      await postStore.cancelOrderAction(order.id)
    }
    if (action === 'confirm') await postStore.confirmOrderAction(order.id)
    message.value = '订单状态已更新。'
  } catch (error) {
    message.value = error.message || '订单操作失败。'
  }
}

onMounted(() => postStore.fetchOrders())
</script>

<template>
  <div class="order-list">
    <div class="list-header">
      <h2>我的订单</h2>
      <button class="btn-outline" @click="postStore.fetchOrders()">刷新</button>
    </div>

    <div class="filters">
      <button v-for="filter in filters" :key="filter.value" :class="{ active: activeFilter === filter.value }" @click="activeFilter = filter.value">
        {{ filter.label }}
      </button>
    </div>

    <p v-if="message" class="message">{{ message }}</p>

    <div v-if="filteredOrders.length" class="orders">
      <article v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-main">
          <div>
            <h3>{{ order.orderNo || `订单 ${order.id}` }}</h3>
            <p>{{ order.time }} · {{ order.statusText || order.status }}</p>
          </div>
          <strong>¥{{ order.total.toFixed(2) }}</strong>
        </div>
        <div class="items">
          <span v-for="item in order.items" :key="item.id || item.name">{{ item.name }} × {{ item.quantity }}</span>
          <span v-if="!order.items?.length">共 {{ order.itemCount || 0 }} 件商品</span>
        </div>
        <div class="order-actions">
          <button v-if="order.status === 'pending'" class="primary" @click="handleOrder(order, 'pay')">支付</button>
          <button v-if="order.status === 'pending'" @click="handleOrder(order, 'cancel')">取消</button>
          <button v-if="order.status === 'paid' || order.status === 'shipped'" class="primary" @click="handleOrder(order, 'confirm')">确认完成</button>
          <button @click="selectedOrder = order">详情</button>
        </div>
      </article>
    </div>

    <div v-else class="empty">
      <p>暂无订单记录。</p>
      <RouterLink to="/member-center" class="btn-primary">去会员中心看看</RouterLink>
    </div>

    <div v-if="selectedOrder" class="modal" @click="selectedOrder = null">
      <div class="modal-box" @click.stop>
        <button class="close" @click="selectedOrder = null">×</button>
        <h2>订单详情</h2>
        <p>订单号：{{ selectedOrder.orderNo || selectedOrder.id }}</p>
        <p>状态：{{ selectedOrder.statusText || selectedOrder.status }}</p>
        <p>创建时间：{{ selectedOrder.createTime || selectedOrder.time }}</p>
        <p>商品数量：{{ selectedOrder.itemCount || selectedOrder.items?.length || 0 }}</p>
        <p>总金额：¥{{ selectedOrder.total.toFixed(2) }}</p>
        <div class="detail-items">
          <div v-for="item in selectedOrder.items" :key="item.id || item.name">
            <img v-if="item.image" :src="item.image" :alt="item.name" />
            <span>{{ item.name }} / {{ item.spec || '默认规格' }} × {{ item.quantity }}</span>
            <strong>¥{{ Number(item.price || 0).toFixed(2) }}</strong>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.list-header,
.order-main,
.order-actions,
.filters {
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-header {
  justify-content: space-between;
  margin-bottom: 20px;
}

.list-header h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

.filters {
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.filters button,
.order-actions button {
  padding: 8px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.filters button.active,
.order-actions button.primary {
  border-color: var(--color-primary);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.message {
  padding: 12px 14px;
  margin-bottom: 16px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.orders {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-card {
  padding: 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.order-main {
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-main h3 {
  color: var(--color-heading);
  margin-bottom: 5px;
}

.order-main p,
.items {
  color: var(--color-text-secondary);
}

.order-main strong {
  color: var(--color-primary-dark);
  font-size: 1.2rem;
}

.items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 0.86rem;
}

.items span {
  padding: 5px 8px;
  border-radius: var(--radius-sm);
  background: var(--color-background-soft);
}

.empty {
  text-align: center;
  padding: 56px 0;
  color: var(--color-text-secondary);
}

.empty p {
  margin-bottom: 18px;
}

.modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.6);
}

.modal-box {
  width: min(560px, 100%);
  position: relative;
  padding: 26px;
  border-radius: var(--radius-lg);
  background: #fff;
}

.modal-box h2 {
  color: var(--color-heading);
  margin-bottom: 14px;
}

.modal-box p {
  color: var(--color-text);
  line-height: 1.8;
}

.detail-items {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-items div {
  display: grid;
  grid-template-columns: 44px 1fr auto;
  align-items: center;
  gap: 10px;
}

.detail-items img {
  width: 44px;
  height: 44px;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 50%;
  background: var(--color-background-soft);
  cursor: pointer;
}

@media (max-width: 640px) {
  .order-main,
  .order-actions {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
