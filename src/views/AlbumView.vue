<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { galleryPhotos } from '@/data/huangzihongfan'

const route = useRoute()
const router = useRouter()
const category = ref(route.query.category ? decodeURIComponent(route.query.category) : '全部')

const categories = ['全部', ...Array.from(new Set(galleryPhotos.map((photo) => photo.category)))]

const photos = computed(() => {
  if (category.value === '全部') return galleryPhotos
  return galleryPhotos.filter((photo) => photo.category === category.value)
})
</script>

<template>
  <div class="album-page">
    <section class="page-hero">
      <span class="eyebrow">ALBUM</span>
      <h1>{{ category === '全部' ? '精选相册' : category }}</h1>
      <p>公开图片素材整理</p>
    </section>

    <main class="container">
      <div class="actions">
        <button class="btn-outline" @click="router.push('/gallery')">返回相册</button>
        <div class="tabs">
          <button v-for="item in categories" :key="item" :class="{ active: category === item }" @click="category = item">
            {{ item }}
          </button>
        </div>
      </div>

      <div class="photo-grid">
        <figure v-for="photo in photos" :key="photo.caption">
          <img :src="photo.src" :alt="photo.caption" />
          <figcaption>{{ photo.caption }}</figcaption>
        </figure>
      </div>
    </main>
  </div>
</template>

<style scoped>
.album-page {
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

.actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 26px;
}

.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tabs button {
  padding: 8px 14px;
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

.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

figure {
  overflow: hidden;
  margin: 0;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
}

figure img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
}

figcaption {
  padding: 12px 14px;
  color: var(--color-heading);
  font-weight: 700;
}

@media (max-width: 800px) {
  .photo-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 520px) {
  .photo-grid {
    grid-template-columns: 1fr;
  }
}
</style>
