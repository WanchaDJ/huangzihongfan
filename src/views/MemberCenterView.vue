<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemberStore } from '@/stores/member'
import { usePostStore } from '@/stores/posts'
import { memberMerchSuggestions, openExternal, sourceNotes } from '@/data/huangzihongfan'
import { resolveAvatar } from '@/utils/avatar'

const auth = useAuthStore()
const memberStore = useMemberStore()
const postStore = usePostStore()
const activeTab = ref('overview')
const message = ref('')

const tabs = [
  { key: 'overview', label: '概览' },
  { key: 'products', label: '会员商品' },
  { key: 'benefits', label: '权益' },
  { key: 'orders', label: '订单' },
  { key: 'points', label: '积分' },
  { key: 'renew', label: '续费' }
]

const username = computed(() => auth.user?.username || auth.user?.userName || '当前用户')
const avatar = computed(() => resolveAvatar(auth.user, username.value))
const memberInfo = computed(() => memberStore.memberInfo)
const orders = computed(() => postStore.orders)
const products = computed(() => {
  if (postStore.products.length) return postStore.products
  return memberMerchSuggestions.map((item, index) => ({
    id: `fallback-${index}`,
    name: item.name,
    title: item.name,
    description: item.description,
    price: item.suggestedPrice,
    originalPrice: item.suggestedPrice,
    stock: 0,
    image: item.image,
    type: 'merch',
    category: item.category,
    favorited: false
  }))
})

const renewPlans = [
  { months: 1, price: 29 },
  { months: 3, price: 79 },
  { months: 12, price: 268 }
]

async function refreshAll() {
  await Promise.allSettled([
    auth.fetchCurrentUser(),
    memberStore.fetchMemberInfo(),
    memberStore.fetchPrivileges(),
    memberStore.fetchPointsHistory(),
    memberStore.fetchGrowthHistory(),
    memberStore.fetchConsumptionHistory(),
    postStore.fetchProducts(),
    postStore.fetchFavoriteProducts(),
    postStore.fetchOrders()
  ])
}

function addProduct(product) {
  if (String(product.id).startsWith('fallback-')) {
    message.value = '当前展示商品需要后端种子数据支持，重启后端后可购买。'
    return
  }
  postStore.addToCart(product)
  message.value = `已加入购物车：${product.name}`
}

async function createProductOrder(product) {
  if (String(product.id).startsWith('fallback-')) {
    message.value = '当前展示商品需要后端种子数据支持，重启后端后可下单。'
    return
  }
  message.value = ''
  try {
    await postStore.createDirectProductOrder(product)
    message.value = '订单已创建，请在订单页完成支付。'
    activeTab.value = 'orders'
  } catch (error) {
    message.value = error?.message || '下单失败，请稍后重试。'
  }
}

async function toggleProductFavorite(product) {
  if (String(product.id).startsWith('fallback-')) {
    message.value = '当前展示商品需要后端种子数据支持，重启后端后可收藏。'
    return
  }
  try {
    await postStore.toggleProductFavorite(product.id)
    message.value = product.favorited ? `已取消收藏：${product.name}` : `已收藏商品：${product.name}`
  } catch (error) {
    message.value = error.message || '商品收藏失败。'
  }
}

async function handleRenew(months) {
  message.value = ''
  try {
    await memberStore.renewMember(months)
    message.value = '会员续费信息已更新。'
  } catch (error) {
    message.value = error.message || '续费失败。'
  }
}

async function payOrder(id) {
  await postStore.payOrderAction(id)
  await memberStore.fetchMemberInfo()
}

async function cancelOrder(id) {
  if (!window.confirm('确定取消订单吗？')) return
  await postStore.cancelOrderAction(id)
}

async function confirmOrder(id) {
  await postStore.confirmOrderAction(id)
}

onMounted(refreshAll)
</script>

<template>
  <div class="member-page">
    <main class="container">
      <section class="member-card">
        <div class="profile">
          <img :src="avatar" alt="用户头像" />
          <div>
            <span class="eyebrow">MEMBER</span>
            <h1>{{ username }}</h1>
            <p>{{ memberInfo.level }}</p>
          </div>
        </div>
        <div class="stats">
          <div><strong>{{ memberInfo.growthValue || 0 }}</strong><span>成长值</span></div>
          <div><strong>{{ memberInfo.points || 0 }}</strong><span>积分</span></div>
          <div><strong>{{ orders.length }}</strong><span>订单</span></div>
          <div><strong>{{ memberStore.remainingDays }}</strong><span>剩余天数</span></div>
        </div>
      </section>

      <nav class="tabs">
        <button v-for="tab in tabs" :key="tab.key" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
          {{ tab.label }}
        </button>
      </nav>

      <p v-if="message" class="message">{{ message }}</p>

      <section v-if="activeTab === 'overview'" class="panel">
        <div class="panel-head">
          <h2>会员概览</h2>
          <button class="btn-outline" @click="refreshAll">刷新数据</button>
        </div>
        <div class="overview-grid">
          <div><span>会员到期</span><strong>{{ memberInfo.expireDate ? String(memberInfo.expireDate).slice(0, 10) : '未开通或未设置' }}</strong></div>
          <div><span>累计消费</span><strong>¥{{ Number(memberInfo.totalSpend || 0).toFixed(2) }}</strong></div>
          <div><span>邀请码</span><strong>{{ memberInfo.inviteCode || '暂无' }}</strong></div>
        </div>
        <p class="note">会员体系为本站功能，不代表黄子弘凡官方会员或商务服务。</p>
      </section>

      <section v-if="activeTab === 'products'" class="panel">
        <div class="panel-head">
          <h2>会员商品</h2>
          <RouterLink class="btn-outline" to="/profile/cart">查看购物车（{{ postStore.cartItemCount }}）</RouterLink>
        </div>
        <div v-if="products.length" class="product-grid">
          <article v-for="product in products" :key="product.id" class="product-card">
            <img :src="product.image" :alt="product.name" />
            <div class="product-body">
              <span>{{ product.category }}</span>
              <h3>{{ product.name }}</h3>
              <p>{{ product.description }}</p>
              <div class="product-meta">
                <strong>¥{{ Number(product.price || 0).toFixed(2) }}</strong>
                <em>库存 {{ product.stock }}</em>
              </div>
              <div class="product-actions">
                <button class="btn-outline" @click="toggleProductFavorite(product)">
                  {{ product.favorited ? '取消收藏' : '收藏商品' }}
                </button>
                <button class="btn-outline" @click="addProduct(product)">加入购物车</button>
                <button class="btn-primary" @click="createProductOrder(product)">直接下单</button>
              </div>
            </div>
          </article>
        </div>
        <p v-else class="empty-text">后端商品数据暂未加载，请确认后端服务和数据库已启动。</p>
      </section>

      <section v-if="activeTab === 'benefits'" class="panel">
        <div class="panel-head">
          <h2>站内会员权益</h2>
        </div>
        <div class="benefit-grid">
          <article v-for="benefit in memberStore.privileges" :key="benefit.id">
            <span>{{ benefit.icon || '权益' }}</span>
            <h3>{{ benefit.name }}</h3>
            <p>{{ benefit.description }}</p>
          </article>
        </div>
      </section>

      <section v-if="activeTab === 'orders'" class="panel">
        <div class="panel-head">
          <h2>我的订单</h2>
          <RouterLink class="btn-outline" to="/profile/orders">订单详情页</RouterLink>
        </div>
        <div v-if="orders.length" class="order-list">
          <article v-for="order in orders" :key="order.id" class="order-card">
            <div>
              <h3>{{ order.orderNo || `订单 ${order.id}` }}</h3>
              <p>{{ order.time }} · {{ order.statusText || order.status }}</p>
            </div>
            <strong>¥{{ order.total.toFixed(2) }}</strong>
            <div class="order-actions">
              <button v-if="order.status === 'pending'" @click="payOrder(order.id)">支付</button>
              <button v-if="order.status === 'pending'" @click="cancelOrder(order.id)">取消</button>
              <button v-if="order.status === 'paid' || order.status === 'shipped'" @click="confirmOrder(order.id)">确认完成</button>
            </div>
          </article>
        </div>
        <div v-else class="empty">
          <p>暂无订单。</p>
          <button class="btn-primary" @click="activeTab = 'products'">查看会员商品</button>
        </div>
      </section>

      <section v-if="activeTab === 'points'" class="panel">
        <div class="panel-head">
          <h2>积分与成长记录</h2>
        </div>
        <div class="record-grid">
          <div>
            <h3>积分明细</h3>
            <article v-for="record in memberStore.pointsHistory" :key="record.id">
              <span>{{ record.date }}</span>
              <strong>{{ record.description }}</strong>
              <em>{{ record.points > 0 ? '+' : '' }}{{ record.points }}</em>
            </article>
            <p v-if="!memberStore.pointsHistory.length" class="empty-text">暂无积分记录。</p>
          </div>
          <div>
            <h3>消费记录</h3>
            <article v-for="record in memberStore.consumptionHistory" :key="record.id">
              <span>{{ record.date }}</span>
              <strong>{{ record.description }}</strong>
              <em>¥{{ Number(record.amount || 0).toFixed(2) }}</em>
            </article>
            <p v-if="!memberStore.consumptionHistory.length" class="empty-text">暂无消费记录。</p>
          </div>
        </div>
      </section>

      <section v-if="activeTab === 'renew'" class="panel">
        <div class="panel-head">
          <h2>会员续费</h2>
        </div>
        <div class="renew-grid">
          <article v-for="plan in renewPlans" :key="plan.months">
            <h3>{{ plan.months }} 个月</h3>
            <strong>¥{{ plan.price }}</strong>
            <button class="btn-primary" @click="handleRenew(plan.months)">确认续费</button>
          </article>
        </div>
        <p class="note">续费会更新本站用户资料中的会员到期时间和成长值，不接入真实支付。</p>
      </section>

      <section class="source-panel">
        <span>资料核验入口</span>
        <button v-for="source in sourceNotes.slice(0, 4)" :key="source.name" @click="openExternal(source.url)">
          {{ source.name }}
        </button>
      </section>
    </main>
  </div>
</template>

<style scoped>
.member-page {
  min-height: 100vh;
  background: var(--color-background);
}

.container {
  padding-top: 34px;
  padding-bottom: 60px;
}

.member-card,
.panel,
.source-panel {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.member-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
  padding: 24px;
  margin-bottom: 20px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile img {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-primary);
}

.eyebrow {
  color: var(--color-primary-dark);
  font-size: 0.75rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.profile h1 {
  color: var(--color-primary-dark);
  font-size: 1.55rem;
  margin: 2px 0 4px;
  font-weight: 800;
}

.profile p,
.stats span,
.note,
.empty-text {
  color: var(--color-text-secondary);
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(80px, 1fr));
  gap: 18px;
}

.stats div {
  text-align: center;
}

.stats strong {
  display: block;
  color: var(--color-primary-dark);
  font-size: 1.35rem;
}

.stats span {
  font-size: 0.8rem;
}

.tabs,
.product-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tabs {
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.tabs button.active,
.btn-primary {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.message {
  padding: 12px 14px;
  margin-bottom: 18px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.panel {
  padding: 24px;
  margin-bottom: 20px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-head h2 {
  color: var(--color-heading);
  font-size: 1.25rem;
}

.overview-grid,
.benefit-grid,
.renew-grid,
.product-grid,
.record-grid {
  display: grid;
  gap: 16px;
}

.overview-grid,
.benefit-grid,
.renew-grid {
  grid-template-columns: repeat(3, 1fr);
}

.record-grid {
  grid-template-columns: repeat(2, 1fr);
}

.product-grid {
  grid-template-columns: repeat(2, 1fr);
}

.overview-grid div,
.benefit-grid article,
.renew-grid article,
.product-card,
.order-card,
.record-grid article {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.overview-grid div,
.benefit-grid article,
.renew-grid article,
.record-grid article {
  padding: 16px;
}

.overview-grid span,
.benefit-grid span {
  color: var(--color-text-secondary);
}

.overview-grid strong,
.benefit-grid h3,
.renew-grid h3,
.product-card h3 {
  color: var(--color-heading);
}

.product-card {
  overflow: hidden;
  background: #fff;
}

.product-card img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
}

.product-body {
  padding: 18px;
}

.product-body > span {
  color: var(--color-primary-dark);
  font-weight: 800;
  font-size: 0.8rem;
}

.product-card p,
.benefit-grid p {
  margin-top: 10px;
  line-height: 1.65;
  color: var(--color-text-secondary);
}

.product-meta,
.order-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.product-meta {
  justify-content: space-between;
  margin: 14px 0;
}

.product-meta strong,
.order-card strong,
.record-grid em,
.renew-grid strong {
  color: var(--color-primary-dark);
  font-style: normal;
  font-weight: 800;
}

.product-meta em {
  color: var(--color-text-secondary);
  font-style: normal;
}

.order-list,
.record-grid > div {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-card {
  display: grid;
  grid-template-columns: 1fr 120px auto;
  align-items: center;
  gap: 12px;
  padding: 14px;
}

.order-card h3 {
  color: var(--color-heading);
  font-size: 0.98rem;
}

.order-card p,
.record-grid span {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.order-actions button,
.source-panel button,
.btn-outline {
  padding: 8px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.empty {
  text-align: center;
  padding: 42px 0;
}

.renew-grid strong {
  display: block;
  margin: 10px 0 16px;
  font-size: 1.8rem;
}

.source-panel {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 16px;
}

.source-panel span {
  color: var(--color-text-secondary);
  font-weight: 700;
}

@media (max-width: 900px) {
  .member-card,
  .panel-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .stats,
  .overview-grid,
  .benefit-grid,
  .renew-grid,
  .product-grid,
  .record-grid {
    width: 100%;
    grid-template-columns: repeat(2, 1fr);
  }

  .order-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .stats,
  .overview-grid,
  .benefit-grid,
  .renew-grid,
  .product-grid,
  .record-grid {
    grid-template-columns: 1fr;
  }
}
</style>
