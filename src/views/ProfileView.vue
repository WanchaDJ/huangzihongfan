<template>
  <div class="profile-page">
    <main class="container">
      <section class="profile-header">
        <img :src="avatar" alt="用户头像" />
        <div>
          <span class="eyebrow">PROFILE</span>
          <h1>个人中心</h1>
          <p>{{ authStore.user?.username || '用户' }}，管理资料、订单、购物车、收藏和安全设置。</p>
        </div>
      </section>

      <div class="profile-layout">
        <aside class="profile-sidebar">
          <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="nav-item">
            <span>{{ item.icon }}</span>
            {{ item.label }}
          </RouterLink>
          <button class="nav-item logout" @click="handleLogout">
            <span>↩</span>
            退出登录
          </button>
        </aside>

        <section class="profile-main">
          <RouterView />
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/posts'
import { useMemberStore } from '@/stores/member'
import { resolveAvatar } from '@/utils/avatar'

const router = useRouter()
const authStore = useAuthStore()
const postStore = usePostStore()
const memberStore = useMemberStore()

const navItems = [
  { to: '/profile', icon: '人', label: '基本信息' },
  { to: '/profile/edit', icon: '编', label: '编辑资料' },
  { to: '/profile/orders', icon: '单', label: '我的订单' },
  { to: '/profile/cart', icon: '车', label: '购物车' },
  { to: '/profile/my-posts', icon: '帖', label: '我的帖子' },
  { to: '/profile/favorites', icon: '藏', label: '我的收藏' },
  { to: '/profile/security', icon: '盾', label: '安全设置' }
]

const avatar = computed(() => resolveAvatar(authStore.user, authStore.user?.username || '用户'))

async function handleLogout() {
  if (!window.confirm('确定退出登录吗？')) return
  await authStore.logout()
  router.push('/login')
}

onMounted(async () => {
  await Promise.allSettled([
    authStore.fetchCurrentUser(),
    memberStore.fetchMemberInfo(),
    postStore.fetchOrders(),
    postStore.fetchMyPosts(),
    postStore.fetchFavoritePosts()
  ])
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--color-background);
}

.container {
  padding-top: 32px;
  padding-bottom: 60px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 24px;
  margin-bottom: 22px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.profile-header img {
  width: 82px;
  height: 82px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-primary);
}

.eyebrow {
  color: var(--color-primary-dark);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.profile-header h1 {
  margin: 4px 0;
  color: var(--color-primary-dark);
  font-size: 2rem;
  font-weight: 800;
}

.profile-header p {
  color: var(--color-text-secondary);
}

.profile-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 22px;
  align-items: start;
}

.profile-sidebar,
.profile-main {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.profile-sidebar {
  padding: 12px;
  position: sticky;
  top: 78px;
}

.nav-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border: 0;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-text);
  text-align: left;
  text-decoration: none;
  font-weight: 700;
  cursor: pointer;
}

.nav-item span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
  font-size: 0.78rem;
  font-weight: 800;
}

.nav-item:hover,
.nav-item.router-link-exact-active {
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.nav-item.logout {
  color: var(--color-danger);
}

.profile-main {
  min-height: 640px;
  padding: 24px;
}

@media (max-width: 820px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 560px) {
  .profile-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .profile-sidebar {
    grid-template-columns: 1fr;
  }
}
</style>
