<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/stores/posts'

const router = useRouter()
const postStore = usePostStore()
const selectedSort = ref('latest')
const activeTab = ref('posts')

const sortedFavoritePosts = computed(() => {
  const list = [...postStore.favoritePosts]
  if (selectedSort.value === 'titleAsc') list.sort((a, b) => a.title.localeCompare(b.title))
  if (selectedSort.value === 'titleDesc') list.sort((a, b) => b.title.localeCompare(a.title))
  return list
})

const sortedFavoriteProducts = computed(() => {
  const list = [...postStore.favoriteProducts]
  if (selectedSort.value === 'titleAsc') list.sort((a, b) => a.name.localeCompare(b.name))
  if (selectedSort.value === 'titleDesc') list.sort((a, b) => b.name.localeCompare(a.name))
  return list
})

async function removeFavoritePost(id) {
  await postStore.toggleFavorite(id)
}

async function removeFavoriteProduct(id) {
  await postStore.toggleProductFavorite(id)
}

onMounted(async () => {
  await Promise.allSettled([postStore.fetchFavoritePosts(), postStore.fetchFavoriteProducts()])
})
</script>

<template>
  <div class="favorites-page">
    <div class="section-header">
      <h2>我的收藏</h2>
      <select v-model="selectedSort">
        <option value="latest">默认排序</option>
        <option value="titleAsc">标题升序</option>
        <option value="titleDesc">标题降序</option>
      </select>
    </div>

    <div class="tabs">
      <button :class="{ active: activeTab === 'posts' }" @click="activeTab = 'posts'">帖子收藏</button>
      <button :class="{ active: activeTab === 'products' }" @click="activeTab = 'products'">商品收藏</button>
    </div>

    <div class="stats">
      <div>
        <strong>{{ postStore.favoritePosts.length }}</strong>
        <span>帖子收藏</span>
      </div>
      <div>
        <strong>{{ postStore.favoriteProducts.length }}</strong>
        <span>商品收藏</span>
      </div>
    </div>

    <div v-if="activeTab === 'posts'">
      <div v-if="sortedFavoritePosts.length" class="favorites-list">
        <article v-for="favorite in sortedFavoritePosts" :key="favorite.id" class="favorite-card">
          <div>
            <h3>{{ favorite.title }}</h3>
            <p class="meta">{{ favorite.username }} · {{ favorite.time }}</p>
            <p>{{ favorite.content }}</p>
          </div>
          <div class="actions">
            <button @click="router.push({ path: '/posts', query: { post: favorite.id } })">查看</button>
            <button class="danger" @click="removeFavoritePost(favorite.id)">取消收藏</button>
          </div>
        </article>
      </div>
      <div v-else class="empty">
        <p>你还没有收藏帖子内容。</p>
        <button class="btn-primary" @click="router.push('/posts')">去社区浏览</button>
      </div>
    </div>

    <div v-else>
      <div v-if="sortedFavoriteProducts.length" class="product-grid">
        <article v-for="product in sortedFavoriteProducts" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name" />
          <div class="product-body">
            <span>{{ product.category }}</span>
            <h3>{{ product.name }}</h3>
            <p>{{ product.description }}</p>
            <strong>¥{{ Number(product.price || 0).toFixed(2) }}</strong>
            <div class="actions row">
              <button @click="router.push('/member-center')">查看商品</button>
              <button class="danger" @click="removeFavoriteProduct(product.id)">取消收藏</button>
            </div>
          </div>
        </article>
      </div>
      <div v-else class="empty">
        <p>你还没有收藏会员商品。</p>
        <button class="btn-primary" @click="router.push('/member-center')">去会员中心</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.section-header,
.tabs,
.stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.section-header {
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-header h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

select {
  padding: 9px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
}

.tabs {
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.tabs button,
.actions button {
  padding: 9px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.tabs button.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.stats {
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stats div {
  padding: 16px 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
}

.stats strong,
.stats span {
  display: block;
}

.stats strong {
  color: var(--color-primary-dark);
  font-size: 1.5rem;
}

.stats span {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.favorite-card {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 18px;
  padding: 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.favorite-card h3,
.product-card h3 {
  color: var(--color-heading);
  margin-bottom: 6px;
}

.favorite-card p,
.product-card p {
  color: var(--color-text);
  line-height: 1.65;
}

.favorite-card .meta {
  color: var(--color-text-secondary);
  font-size: 0.84rem;
  margin-bottom: 8px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.actions.row {
  flex-direction: row;
}

.actions .danger {
  color: var(--color-danger);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.product-card {
  overflow: hidden;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
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

.product-body strong {
  display: block;
  margin: 12px 0 14px;
  color: var(--color-primary-dark);
}

.empty {
  text-align: center;
  padding: 56px 0;
  color: var(--color-text-secondary);
}

.empty p {
  margin-bottom: 18px;
}

@media (max-width: 720px) {
  .favorite-card {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
