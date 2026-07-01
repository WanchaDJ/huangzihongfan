<template>
  <div class="my-posts">
    <div class="section-head">
      <h2>我的帖子</h2>
      <button class="btn-outline" @click="postStore.fetchMyPosts()">刷新</button>
    </div>

    <div class="stats">
      <div><strong>{{ postStore.userPosts.length }}</strong><span>发布总数</span></div>
      <div><strong>{{ totalLikes }}</strong><span>收到点赞</span></div>
      <div><strong>{{ totalFavourites }}</strong><span>收到收藏</span></div>
    </div>

    <div v-if="postStore.userPosts.length" class="posts-list">
      <article v-for="post in postStore.userPosts" :key="post.id" class="post-item">
        <div>
          <h3>{{ post.title }}</h3>
          <p class="meta">{{ post.time }} · 点赞 {{ post.likes }} · 收藏 {{ post.favourNum }}</p>
          <p>{{ post.content }}</p>
        </div>
        <div class="actions">
          <button @click="router.push({ path: '/posts', query: { post: post.id } })">查看</button>
          <button class="danger" @click="deletePost(post.id)">删除</button>
        </div>
      </article>
    </div>

    <div v-else class="empty">
      <p>你还没有发表任何帖子。</p>
      <button class="btn-primary" @click="router.push('/posts')">去发布</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/stores/posts'

const router = useRouter()
const postStore = usePostStore()

const totalLikes = computed(() => postStore.userPosts.reduce((sum, post) => sum + Number(post.likes || 0), 0))
const totalFavourites = computed(() => postStore.userPosts.reduce((sum, post) => sum + Number(post.favourNum || 0), 0))

async function deletePost(postId) {
  if (!window.confirm('确定删除这篇帖子吗？')) return
  await postStore.deletePost(postId)
}

onMounted(() => postStore.fetchMyPosts())
</script>

<style scoped>
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.section-head h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-bottom: 22px;
}

.stats div {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.stats strong,
.stats span {
  display: block;
}

.stats strong {
  color: var(--color-primary-dark);
  font-size: 1.35rem;
}

.stats span {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-item {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 18px;
  padding: 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.post-item h3 {
  color: var(--color-heading);
  margin-bottom: 6px;
}

.post-item p {
  color: var(--color-text);
  line-height: 1.65;
}

.post-item .meta {
  color: var(--color-text-secondary);
  font-size: 0.84rem;
  margin-bottom: 8px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.actions button {
  padding: 9px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.actions .danger {
  color: var(--color-danger);
}

.empty {
  text-align: center;
  padding: 56px 0;
  color: var(--color-text-secondary);
}

.empty p {
  margin-bottom: 18px;
}

@media (max-width: 640px) {
  .stats,
  .post-item {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: row;
  }
}
</style>
