<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="nav-inner">
      <RouterLink to="/" class="brand" @click="closeMobile">
        <span class="brand-mark">HZ</span>
        <span class="brand-text">黄子弘凡</span>
      </RouterLink>

      <div class="nav-links">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="nav-link">
          {{ item.label }}
        </RouterLink>
      </div>

      <div class="nav-actions">
        <template v-if="isAuthenticated">
          <div class="user-menu" @mouseenter="keepDropdown" @mouseleave="scheduleClose">
            <button class="user-trigger" @click="toggleDropdown">
              <img :src="avatar" class="avatar" alt="用户头像" />
              <span>{{ currentUser?.username || currentUser?.userName || '用户' }}</span>
              <em v-if="unreadCount" class="message-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</em>
            </button>
            <div v-show="showDropdown" class="dropdown" @mouseenter="keepDropdown" @mouseleave="scheduleClose">
              <RouterLink to="/profile" @click="closeDropdown">个人中心</RouterLink>
              <RouterLink to="/profile/orders" @click="closeDropdown">我的订单</RouterLink>
              <RouterLink to="/profile/my-posts" @click="closeDropdown">我的帖子</RouterLink>
              <RouterLink to="/profile/favorites" @click="closeDropdown">我的收藏</RouterLink>
              <RouterLink to="/member-center" @click="closeDropdown">会员中心</RouterLink>
              <RouterLink to="/chat" class="message-link" @click="handleOpenChat">
                私信
                <em v-if="unreadCount" class="inline-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</em>
              </RouterLink>
              <button class="logout" @click="doLogout">退出登录</button>
            </div>
          </div>
        </template>
        <template v-else>
          <RouterLink to="/login" class="login-link">登录</RouterLink>
          <RouterLink to="/register" class="register-link">注册</RouterLink>
        </template>
      </div>

      <button class="mobile-button" :class="{ open: mobileOpen }" aria-label="打开菜单" @click="toggleMobile">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>

    <div class="mobile-nav" :class="{ active: mobileOpen }">
      <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="mobile-link" @click="closeMobile">
        {{ item.label }}
      </RouterLink>
      <div class="mobile-divider"></div>
      <template v-if="isAuthenticated">
        <RouterLink to="/profile" class="mobile-link" @click="closeMobile">个人中心</RouterLink>
        <RouterLink to="/chat" class="mobile-link" @click="handleOpenChat">
          私信
          <em v-if="unreadCount" class="inline-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</em>
        </RouterLink>
        <button class="mobile-link danger" @click="doLogout">退出登录</button>
      </template>
      <template v-else>
        <RouterLink to="/login" class="mobile-link" @click="closeMobile">登录</RouterLink>
        <RouterLink to="/register" class="mobile-link highlight" @click="closeMobile">注册</RouterLink>
      </template>
    </div>
    <div v-if="mobileOpen" class="mobile-overlay" @click="closeMobile"></div>
  </nav>
</template>

<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getUnreadMessageCount } from '@/api/userMessage'
import { resolveAvatar } from '@/utils/avatar'

defineProps({ isScrolled: { type: Boolean, default: false } })

const router = useRouter()
const auth = useAuthStore()
const mobileOpen = ref(false)
const showDropdown = ref(false)
const unreadCount = ref(0)
let dropdownTimer = null
let unreadTimer = null

const navItems = [
  { to: '/', label: '首页' },
  { to: '/about', label: '关于' },
  { to: '/news', label: '动态' },
  { to: '/gallery', label: '相册' },
  { to: '/music-album', label: '音乐' },
  { to: '/tickets', label: '演出' },
  { to: '/posts', label: '社区' },
  { to: '/contact', label: '联系' },
  { to: '/member-center', label: '会员' }
]

const isAuthenticated = computed(() => auth.isAuthenticated)
const currentUser = computed(() => auth.user)
const avatar = computed(() => resolveAvatar(currentUser.value, currentUser.value?.username || '用户'))

function toggleMobile() {
  mobileOpen.value = !mobileOpen.value
  document.body.style.overflow = mobileOpen.value ? 'hidden' : ''
}

function closeMobile() {
  mobileOpen.value = false
  document.body.style.overflow = ''
}

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

function keepDropdown() {
  if (dropdownTimer) {
    clearTimeout(dropdownTimer)
    dropdownTimer = null
  }
}

function scheduleClose() {
  keepDropdown()
  dropdownTimer = window.setTimeout(() => {
    showDropdown.value = false
  }, 300)
}

function closeDropdown() {
  keepDropdown()
  showDropdown.value = false
}

async function refreshUnreadCount() {
  if (!auth.isAuthenticated) {
    unreadCount.value = 0
    return
  }
  try {
    const res = await getUnreadMessageCount()
    unreadCount.value = Number(res.data || 0)
  } catch {
    unreadCount.value = 0
  }
}

async function handleOpenChat() {
  closeDropdown()
  closeMobile()
  unreadCount.value = 0
  await router.push('/chat')
}

async function doLogout() {
  closeDropdown()
  closeMobile()
  await auth.logout()
  unreadCount.value = 0
  router.push('/')
}

watch(
  () => auth.isAuthenticated,
  async (value) => {
    if (!value) {
      unreadCount.value = 0
      if (unreadTimer) clearInterval(unreadTimer)
      unreadTimer = null
      return
    }
    await refreshUnreadCount()
    if (!unreadTimer) {
      unreadTimer = window.setInterval(refreshUnreadCount, 30000)
    }
  },
  { immediate: true }
)

onMounted(() => {
  if (auth.isAuthenticated) {
    refreshUnreadCount()
  }
})

onBeforeUnmount(() => {
  if (dropdownTimer) clearTimeout(dropdownTimer)
  if (unreadTimer) clearInterval(unreadTimer)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid var(--color-border);
  transition: box-shadow 0.25s ease;
}

.navbar.scrolled {
  box-shadow: var(--shadow-sm);
}

.nav-inner {
  max-width: 1280px;
  height: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  position: relative;
  z-index: 1002;
}

.brand {
  display: flex;
  align-items: center;
  gap: 9px;
  text-decoration: none;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: #17130e;
  font-size: 0.78rem;
  font-weight: 900;
}

.brand-text {
  color: var(--color-heading);
  font-size: 1.15rem;
  font-weight: 800;
  white-space: nowrap;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 2px;
}

.nav-link {
  padding: 8px 11px;
  border-radius: var(--radius-md);
  color: var(--color-text);
  font-size: 0.86rem;
  font-weight: 600;
  text-decoration: none;
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--color-primary-dark);
  background: var(--color-primary-pale);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-link,
.register-link {
  padding: 7px 15px;
  border-radius: var(--radius-md);
  font-size: 0.84rem;
  font-weight: 700;
  text-decoration: none;
}

.login-link {
  border: 1px solid var(--color-primary);
  color: var(--color-primary-dark);
}

.register-link {
  background: var(--color-primary);
  color: #17130e;
}

.user-menu {
  position: relative;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px;
  border: 0;
  border-radius: var(--radius-md);
  background: transparent;
  cursor: pointer;
  position: relative;
}

.user-trigger:hover {
  background: var(--color-primary-pale);
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-primary);
}

.user-trigger span {
  max-width: 82px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-heading);
  font-size: 0.84rem;
  font-weight: 700;
}

.message-badge,
.inline-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 999px;
  background: #d93c2f;
  color: #fff;
  font-style: normal;
  font-size: 0.72rem;
  font-weight: 800;
}

.message-badge {
  position: absolute;
  top: -4px;
  right: 0;
}

.dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  min-width: 210px;
  padding: 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
  box-shadow: var(--shadow-lg);
}

.dropdown a,
.dropdown button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border: 0;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-text);
  text-align: left;
  text-decoration: none;
  cursor: pointer;
}

.dropdown a:hover,
.dropdown button:hover {
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.dropdown .logout {
  color: var(--color-danger);
}

.mobile-button {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 8px;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.mobile-button span {
  display: block;
  width: 22px;
  height: 2px;
  border-radius: 2px;
  background: var(--color-heading);
  transition: transform 0.25s ease, opacity 0.25s ease;
}

.mobile-button.open span:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.mobile-button.open span:nth-child(2) {
  opacity: 0;
}

.mobile-button.open span:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

.mobile-nav {
  position: fixed;
  top: 0;
  right: 0;
  width: min(300px, 86vw);
  height: 100vh;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 80px 22px 24px;
  background: #fff;
  box-shadow: var(--shadow-xl);
  overflow-y: auto;
  transform: translateX(100%);
  transition: transform 0.28s ease;
}

.mobile-nav.active {
  transform: translateX(0);
}

.mobile-link {
  width: 100%;
  padding: 12px 14px;
  border: 0;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-text);
  text-align: left;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mobile-link:hover,
.mobile-link.router-link-active {
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.mobile-link.highlight {
  background: var(--color-primary);
  color: #17130e;
  text-align: center;
  justify-content: center;
}

.mobile-link.danger {
  color: var(--color-danger);
}

.mobile-divider {
  height: 1px;
  margin: 8px 0;
  background: var(--color-border);
}

.mobile-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.35);
}

@media (max-width: 1050px) {
  .nav-links,
  .nav-actions {
    display: none;
  }

  .mobile-button {
    display: flex;
  }
}
</style>
