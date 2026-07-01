<template>
  <div class="user-profile">
    <div class="section-head">
      <h2>基本信息</h2>
      <RouterLink class="btn-outline" to="/profile/edit">编辑资料</RouterLink>
    </div>

    <div class="summary-grid">
      <div>
        <span>会员等级</span>
        <strong>{{ memberStore.memberInfo.level }}</strong>
      </div>
      <div>
        <span>积分</span>
        <strong>{{ memberStore.memberInfo.points }}</strong>
      </div>
      <div>
        <span>成长值</span>
        <strong>{{ memberStore.memberInfo.growthValue }}</strong>
      </div>
      <div>
        <span>订单</span>
        <strong>{{ postStore.orders.length }}</strong>
      </div>
    </div>

    <div class="info-grid">
      <div class="info-item">
        <span>昵称</span>
        <strong>{{ authStore.user?.username || '用户' }}</strong>
      </div>
      <div class="info-item">
        <span>账号</span>
        <strong>{{ authStore.user?.userAccount || '未设置' }}</strong>
      </div>
      <div class="info-item">
        <span>手机号</span>
        <strong>{{ memberStore.memberInfo.phone || '未设置' }}</strong>
      </div>
      <div class="info-item">
        <span>生日</span>
        <strong>{{ formatDate(memberStore.memberInfo.birthday) || '未设置' }}</strong>
      </div>
      <div class="info-item wide">
        <span>地址</span>
        <strong>{{ address || '未设置' }}</strong>
      </div>
      <div class="info-item wide">
        <span>个人简介</span>
        <strong>{{ memberStore.memberInfo.bio || authStore.user?.userProfile || '还没有填写简介' }}</strong>
      </div>
    </div>

    <div class="quick-actions">
      <RouterLink to="/profile/orders">查看订单</RouterLink>
      <RouterLink to="/profile/cart">购物车</RouterLink>
      <RouterLink to="/profile/my-posts">我的帖子</RouterLink>
      <RouterLink to="/profile/favorites">我的收藏</RouterLink>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemberStore } from '@/stores/member'
import { usePostStore } from '@/stores/posts'

const authStore = useAuthStore()
const memberStore = useMemberStore()
const postStore = usePostStore()

const address = computed(() => {
  return [
    memberStore.memberInfo.province,
    memberStore.memberInfo.city,
    memberStore.memberInfo.district,
    memberStore.memberInfo.detailAddress
  ].filter(Boolean).join(' ')
})

function formatDate(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value).slice(0, 10)
  return date.toLocaleDateString('zh-CN')
}

onMounted(async () => {
  await Promise.allSettled([
    memberStore.fetchMemberInfo(),
    postStore.fetchOrders(),
    postStore.fetchMyPosts(),
    postStore.fetchFavoritePosts()
  ])
})
</script>

<style scoped>
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
}

.section-head h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

.summary-grid,
.info-grid {
  display: grid;
  gap: 14px;
}

.summary-grid {
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 22px;
}

.summary-grid div,
.info-item {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.summary-grid span,
.info-item span {
  display: block;
  color: var(--color-text-secondary);
  font-size: 0.82rem;
  margin-bottom: 6px;
}

.summary-grid strong {
  display: block;
  color: var(--color-primary-dark);
  font-size: 1.35rem;
}

.info-grid {
  grid-template-columns: repeat(2, 1fr);
}

.info-item strong {
  color: var(--color-heading);
  font-weight: 700;
  word-break: break-word;
}

.info-item.wide {
  grid-column: 1 / -1;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 24px;
}

.quick-actions a {
  padding: 10px 14px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
  text-decoration: none;
}

@media (max-width: 720px) {
  .summary-grid,
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
