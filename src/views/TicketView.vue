<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { createOrder } from '@/api/order'
import { sendContactMessage } from '@/api/contact'
import {
  events as verifiedEvents,
  formatDateTime,
  normalizeEvent,
  officialLinks,
  openExternal
} from '@/data/huangzihongfan'

const router = useRouter()
const auth = useAuthStore()

const selectedCity = ref('全部')
const events = ref(verifiedEvents.map(normalizeEvent))
const selectedEvent = ref(null)
const buyEvent = ref(null)
const selectedTier = ref(null)
const quantity = ref(1)
const message = ref('')
const reminderEmail = ref('')
const submittingReminder = ref(false)
const buying = ref(false)

const cities = computed(() => ['全部', ...Array.from(new Set(events.value.map((event) => event.city).filter(Boolean)))])

const filteredEvents = computed(() => {
  if (selectedCity.value === '全部') return events.value
  return events.value.filter((event) => event.city === selectedCity.value)
})

const saleEvents = computed(() => events.value.filter((event) => ['ON_SALE', 'PRESALE'].includes(event.status)))

function statusText(status) {
  const statusMap = {
    ON_SALE: '在售',
    PRESALE: '预售',
    UPCOMING: '待公开',
    SOLD_OUT: '已售罄',
    ENDED: '已结束',
    CANCELLED: '已取消'
  }
  return statusMap[status] || status
}

function typeText(type) {
  const typeMap = {
    concert: '演唱会',
    meeting: '见面会',
    musical: '音乐剧',
    festival: '音乐节',
    event: '活动'
  }
  return typeMap[type] || '演出'
}

function showDetail(event) {
  selectedEvent.value = event
}

function closeDetail() {
  selectedEvent.value = null
}

function openBuy(event) {
  const tiers = event.priceTiers || []
  if (!['ON_SALE', 'PRESALE'].includes(event.status) || tiers.length === 0) {
    showDetail(event)
    return
  }
  if (!auth.isAuthenticated) {
    router.push('/login?redirect=/tickets')
    return
  }
  buyEvent.value = event
  selectedTier.value = tiers[0]
  quantity.value = 1
  message.value = ''
}

function closeBuy() {
  buyEvent.value = null
  selectedTier.value = null
  quantity.value = 1
}

async function doBuy() {
  if (!buyEvent.value || !selectedTier.value) return
  buying.value = true
  message.value = ''
  try {
    await createOrder({
      eventId: buyEvent.value.id,
      price: String(selectedTier.value.price),
      quantity: quantity.value,
      spec: selectedTier.value.description || '票档'
    })
    message.value = '订单已创建，请到个人中心查看并完成后续操作。'
    closeBuy()
  } catch (error) {
    message.value = error.message || '下单失败，请稍后重试。'
  } finally {
    buying.value = false
  }
}

async function subscribeReminder() {
  if (!reminderEmail.value.trim()) {
    message.value = '请输入用于接收提醒的邮箱。'
    return
  }
  submittingReminder.value = true
  message.value = ''
  try {
    await sendContactMessage({
      name: '票务提醒订阅',
      email: reminderEmail.value.trim(),
      subject: '黄子弘凡演出票务提醒',
      message: '请在黄子弘凡有新的官方公开售票信息时通知我。'
    })
    message.value = '订阅成功，新的官方售票信息公开后会用于提醒。'
    reminderEmail.value = ''
  } catch (error) {
    message.value = '订阅失败，请确认后端服务已启动后重试。'
  } finally {
    submittingReminder.value = false
  }
}

onMounted(() => {
  // Public display intentionally keeps the verified event list as canonical.
  // Back-end order/contact APIs are used for purchase flow and reminders.
})
</script>

<template>
  <div class="ticket-page">
    <section class="page-hero">
      <span class="eyebrow">TICKETS</span>
      <h1>演出与票务</h1>
      <p>当前本站不展示虚构售票场次；实时售票以官方平台和票务平台为准</p>
    </section>

    <main class="container">
      <section class="sale-summary">
        <div>
          <span class="summary-label">当前在售</span>
          <strong>{{ saleEvents.length }}</strong>
          <p v-if="saleEvents.length === 0">暂无已核验的官方在售个人演出。</p>
          <p v-else>后端返回了可下单场次，请购票前再次核对官方平台。</p>
        </div>
        <div class="summary-actions">
          <button class="btn-primary" @click="openExternal(officialLinks.damaiSearch)">查看大麦搜索</button>
          <button class="btn-outline" @click="openExternal(officialLinks.weibo)">查看微博动态</button>
        </div>
      </section>

      <section class="reminder-card">
        <div>
          <h2>订阅开票提醒</h2>
          <p>邮箱会通过后端联系消息接口保存，用于新的官方售票信息提醒。</p>
        </div>
        <form class="reminder-form" @submit.prevent="subscribeReminder">
          <input v-model="reminderEmail" type="email" placeholder="邮箱地址" required />
          <button type="submit" :disabled="submittingReminder">{{ submittingReminder ? '提交中' : '订阅' }}</button>
        </form>
      </section>

      <p v-if="message" class="message">{{ message }}</p>

      <div class="city-bar">
        <span>城市</span>
        <button
          v-for="city in cities"
          :key="city"
          :class="{ active: selectedCity === city }"
          @click="selectedCity = city"
        >
          {{ city }}
        </button>
      </div>

      <section class="event-grid">
        <article v-for="event in filteredEvents" :key="event.id" class="event-card">
          <div class="event-image">
            <img :src="event.coverImage" :alt="event.title" />
            <span class="event-type">{{ typeText(event.type) }}</span>
            <span class="event-status" :class="event.status.toLowerCase()">{{ statusText(event.status) }}</span>
          </div>
          <div class="event-body">
            <div class="event-meta">
              <span>{{ event.city }}</span>
              <span>{{ formatDateTime(event.eventDate) }}</span>
            </div>
            <h2>{{ event.title }}</h2>
            <p class="venue">{{ event.venue }}</p>
            <p class="desc">{{ event.description }}</p>
            <div class="event-actions">
              <button class="btn-primary" @click="showDetail(event)">查看详情</button>
              <button
                v-if="['ON_SALE', 'PRESALE'].includes(event.status)"
                class="btn-outline"
                @click="openBuy(event)"
              >
                站内下单
              </button>
              <button v-else class="btn-outline" @click="openExternal(officialLinks.damaiSearch)">官方票务</button>
            </div>
          </div>
        </article>
      </section>
    </main>

    <div v-if="selectedEvent" class="modal" @click="closeDetail">
      <div class="modal-box" @click.stop>
        <button class="modal-close" @click="closeDetail">×</button>
        <img :src="selectedEvent.coverImage" :alt="selectedEvent.title" />
        <div class="modal-content">
          <span class="event-status inline" :class="selectedEvent.status.toLowerCase()">
            {{ statusText(selectedEvent.status) }}
          </span>
          <h2>{{ selectedEvent.title }}</h2>
          <p>{{ selectedEvent.city }} · {{ selectedEvent.venue }}</p>
          <p>{{ formatDateTime(selectedEvent.eventDate) }}</p>
          <p class="desc">{{ selectedEvent.description }}</p>
          <div class="modal-actions">
            <button class="btn-primary" @click="openExternal(officialLinks.damaiSearch)">票务平台搜索</button>
            <button class="btn-outline" @click="openExternal(officialLinks.weibo)">官方动态</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="buyEvent" class="modal" @click="closeBuy">
      <div class="modal-box buy-box" @click.stop>
        <button class="modal-close" @click="closeBuy">×</button>
        <div class="modal-content">
          <h2>{{ buyEvent.title }}</h2>
          <p>{{ buyEvent.city }} · {{ buyEvent.venue }} · {{ formatDateTime(buyEvent.eventDate) }}</p>
          <h3>选择票档</h3>
          <div class="tiers">
            <button
              v-for="tier in buyEvent.priceTiers"
              :key="tier.price"
              :class="{ active: selectedTier?.price === tier.price }"
              @click="selectedTier = tier"
            >
              <strong>{{ tier.price }} 元</strong>
              <span>{{ tier.description || '票档' }}</span>
            </button>
          </div>
          <h3>数量</h3>
          <div class="qty-row">
            <button @click="quantity > 1 && quantity--">-</button>
            <span>{{ quantity }}</span>
            <button @click="quantity < 5 && quantity++">+</button>
          </div>
          <div class="total">
            合计：{{ (selectedTier?.price || 0) * quantity }} 元
          </div>
          <button class="btn-primary confirm" :disabled="buying" @click="doBuy">
            {{ buying ? '创建订单中' : '确认下单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ticket-page {
  min-height: 100vh;
  background: var(--color-background);
}

.page-hero {
  padding: 56px 24px;
  text-align: center;
  color: #fff;
  background: linear-gradient(135deg, #1a1815 0%, #2d2418 50%, #19152c 100%);
}

.eyebrow {
  color: var(--color-primary);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.page-hero h1 {
  margin: 8px 0;
  font-size: 2.35rem;
}

.page-hero p {
  color: rgba(255, 255, 255, 0.72);
}

.container {
  padding-top: 34px;
  padding-bottom: 60px;
}

.sale-summary,
.reminder-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 22px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
  margin-bottom: 20px;
}

.summary-label {
  display: block;
  color: var(--color-text-secondary);
  font-size: 0.8rem;
  margin-bottom: 4px;
}

.sale-summary strong {
  display: block;
  color: var(--color-primary-dark);
  font-size: 2.1rem;
}

.sale-summary p,
.reminder-card p {
  color: var(--color-text-secondary);
  line-height: 1.65;
}

.summary-actions,
.event-actions,
.modal-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.reminder-card h2 {
  color: var(--color-heading);
  margin-bottom: 6px;
  font-size: 1.2rem;
}

.reminder-form {
  display: flex;
  gap: 10px;
  min-width: min(460px, 100%);
}

.reminder-form input {
  flex: 1;
  min-width: 0;
  padding: 11px 13px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.reminder-form button {
  padding: 11px 18px;
  border: 0;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 700;
  cursor: pointer;
}

.message {
  padding: 12px 14px;
  margin-bottom: 18px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.city-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin: 24px 0;
}

.city-bar span {
  color: var(--color-heading);
  font-weight: 700;
}

.city-bar button {
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.city-bar button.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #17130e;
  font-weight: 700;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 22px;
}

.event-card {
  overflow: hidden;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.event-card:hover {
  transform: translateY(-4px);
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.event-image {
  position: relative;
  height: 210px;
  overflow: hidden;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-type,
.event-status {
  position: absolute;
  top: 12px;
  padding: 5px 10px;
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  font-weight: 800;
}

.event-type {
  left: 12px;
  background: rgba(255, 255, 255, 0.92);
  color: var(--color-primary-dark);
}

.event-status {
  right: 12px;
  background: #6b7280;
  color: #fff;
}

.event-status.on_sale,
.event-status.presale {
  background: #16a34a;
}

.event-status.ended {
  background: #4b5563;
}

.event-status.inline {
  position: static;
  display: inline-flex;
  margin-bottom: 12px;
}

.event-body {
  padding: 19px;
}

.event-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--color-text-secondary);
  font-size: 0.82rem;
  margin-bottom: 8px;
}

.event-body h2 {
  color: var(--color-heading);
  font-size: 1.1rem;
  line-height: 1.45;
  margin-bottom: 8px;
}

.venue,
.desc {
  color: var(--color-text-secondary);
  line-height: 1.65;
  font-size: 0.88rem;
}

.desc {
  margin: 10px 0 16px;
}

.modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.66);
}

.modal-box {
  width: min(760px, 100%);
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  border-radius: var(--radius-lg);
  background: #fff;
}

.modal-box img {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.modal-content {
  padding: 24px;
}

.modal-content h2 {
  color: var(--color-heading);
  margin-bottom: 8px;
}

.modal-content h3 {
  margin: 22px 0 10px;
  color: var(--color-heading);
  font-size: 1rem;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  font-size: 1.4rem;
  cursor: pointer;
}

.buy-box {
  max-width: 560px;
}

.tiers {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.tiers button {
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.tiers button.active {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
}

.tiers strong,
.tiers span {
  display: block;
}

.tiers span {
  margin-top: 4px;
  color: var(--color-text-secondary);
  font-size: 0.8rem;
}

.qty-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.qty-row button {
  width: 36px;
  height: 36px;
  border: 1px solid var(--color-border);
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
}

.qty-row span {
  font-weight: 800;
  min-width: 32px;
  text-align: center;
}

.total {
  margin: 18px 0;
  padding: 14px;
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
  color: var(--color-heading);
  font-weight: 800;
}

.confirm {
  width: 100%;
}

@media (max-width: 960px) {
  .sale-summary,
  .reminder-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .event-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .event-grid,
  .tiers {
    grid-template-columns: 1fr;
  }

  .event-meta,
  .reminder-form {
    flex-direction: column;
  }
}
</style>
