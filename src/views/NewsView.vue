<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  newsItems,
  events,
  profile,
  sourceNotes,
  officialLinks,
  formatDateTime,
  openExternal
} from '@/data/huangzihongfan'

const router = useRouter()
const searchText = ref('')
const activeCategory = ref('全部')
const selectedItem = ref(null)

const categories = computed(() => ['全部', ...Array.from(new Set(newsItems.map((item) => item.category)))])

const filteredNews = computed(() => {
  const keyword = searchText.value.trim().toLowerCase()
  return newsItems.filter((item) => {
    const matchCategory = activeCategory.value === '全部' || item.category === activeCategory.value
    const matchKeyword = !keyword || `${item.title}${item.summary}${item.category}`.toLowerCase().includes(keyword)
    return matchCategory && matchKeyword
  })
})

const timelineEvents = computed(() => events.slice().sort((a, b) => new Date(b.eventDate) - new Date(a.eventDate)))

function showNews(item) {
  selectedItem.value = item
}

function closeNews() {
  selectedItem.value = null
}
</script>

<template>
  <div class="news-page">
    <section class="page-hero">
      <span class="eyebrow">NEWS</span>
      <h1>最新动态</h1>
      <p>公开资料整理，实时行程以官方平台为准</p>
    </section>

    <main class="container news-layout">
      <aside class="sidebar">
        <div class="profile-card">
          <img :src="profile.avatar" :alt="profile.name" />
          <h2>{{ profile.name }}</h2>
          <p>{{ profile.occupations.join(' / ') }}</p>
          <button class="btn-primary" @click="openExternal(officialLinks.weibo)">查看微博主页</button>
        </div>

        <div class="sidebar-card">
          <h3>快速入口</h3>
          <button @click="router.push('/music-album')">音乐作品</button>
          <button @click="router.push('/tickets')">演出与票务</button>
          <button @click="router.push('/gallery')">精选相册</button>
          <button @click="router.push('/posts')">粉丝社区</button>
        </div>

        <div class="sidebar-card">
          <h3>资料来源</h3>
          <button v-for="source in sourceNotes.slice(0, 4)" :key="source.name" @click="openExternal(source.url)">
            {{ source.name }}
          </button>
        </div>
      </aside>

      <section class="content">
        <div class="tools">
          <div class="search-box">
            <input v-model="searchText" type="search" placeholder="搜索动态、歌曲或活动" />
          </div>
          <div class="category-tabs">
            <button
              v-for="category in categories"
              :key="category"
              :class="{ active: activeCategory === category }"
              @click="activeCategory = category"
            >
              {{ category }}
            </button>
          </div>
        </div>

        <section class="news-list">
          <article v-for="item in filteredNews" :key="item.id" class="news-card">
            <img :src="item.coverImage" :alt="item.title" />
            <div class="news-body">
              <div class="news-meta">
                <span>{{ item.category }}</span>
                <time>{{ item.date }}</time>
              </div>
              <h2>{{ item.title }}</h2>
              <p>{{ item.summary }}</p>
              <div class="card-actions">
                <button class="btn-primary" @click="showNews(item)">查看详情</button>
                <button class="btn-outline" @click="openExternal(item.sourceUrl)">打开来源</button>
              </div>
            </div>
          </article>
          <p v-if="filteredNews.length === 0" class="empty">没有匹配的动态。</p>
        </section>

        <section class="timeline-section">
          <div class="section-title">
            <h2>演出与活动记录</h2>
            <button class="btn-outline" @click="router.push('/tickets')">进入票务页</button>
          </div>
          <div class="timeline">
            <button v-for="event in timelineEvents" :key="event.id" class="timeline-item" @click="router.push('/tickets')">
              <span class="date">{{ formatDateTime(event.eventDate) }}</span>
              <strong>{{ event.title }}</strong>
              <span>{{ event.city }} · {{ event.venue }}</span>
            </button>
          </div>
        </section>
      </section>
    </main>

    <div v-if="selectedItem" class="modal" @click="closeNews">
      <div class="modal-box" @click.stop>
        <button class="modal-close" @click="closeNews">×</button>
        <img :src="selectedItem.coverImage" :alt="selectedItem.title" />
        <div class="modal-content">
          <span class="category">{{ selectedItem.category }}</span>
          <h2>{{ selectedItem.title }}</h2>
          <time>{{ selectedItem.date }}</time>
          <p>{{ selectedItem.summary }}</p>
          <div class="card-actions">
            <button class="btn-primary" @click="openExternal(selectedItem.sourceUrl)">打开来源</button>
            <button class="btn-outline" @click="closeNews">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.news-page {
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

.news-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 28px;
  padding-top: 34px;
  padding-bottom: 60px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.profile-card,
.sidebar-card,
.news-card,
.timeline-section {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.profile-card {
  padding: 22px;
  text-align: center;
}

.profile-card img {
  width: 104px;
  height: 104px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 14px;
}

.profile-card h2 {
  color: var(--color-heading);
  font-size: 1.35rem;
  margin-bottom: 6px;
}

.profile-card p {
  color: var(--color-text-secondary);
  margin-bottom: 16px;
  font-size: 0.88rem;
}

.sidebar-card {
  padding: 18px;
}

.sidebar-card h3 {
  color: var(--color-heading);
  font-size: 1rem;
  margin-bottom: 12px;
}

.sidebar-card button {
  width: 100%;
  display: block;
  margin-bottom: 8px;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  text-align: left;
  cursor: pointer;
}

.sidebar-card button:hover {
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
}

.tools {
  margin-bottom: 22px;
}

.search-box input {
  width: 100%;
  padding: 13px 15px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  outline: none;
}

.search-box input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-pale);
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.category-tabs button {
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.category-tabs button.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #17130e;
  font-weight: 700;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-card {
  display: grid;
  grid-template-columns: 220px 1fr;
  overflow: hidden;
}

.news-card img {
  width: 100%;
  height: 100%;
  min-height: 190px;
  object-fit: cover;
}

.news-body {
  padding: 20px;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--color-text-secondary);
  font-size: 0.82rem;
  margin-bottom: 8px;
}

.news-meta span,
.category {
  color: var(--color-primary-dark);
  font-weight: 800;
}

.news-body h2 {
  color: var(--color-heading);
  font-size: 1.18rem;
  line-height: 1.45;
  margin-bottom: 8px;
}

.news-body p,
.modal-content p {
  color: var(--color-text-secondary);
  line-height: 1.7;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.empty {
  padding: 50px 0;
  color: var(--color-text-secondary);
  text-align: center;
}

.timeline-section {
  margin-top: 26px;
  padding: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.section-title h2 {
  color: var(--color-heading);
  font-size: 1.2rem;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 150px 1fr 180px;
  gap: 12px;
  align-items: center;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
  text-align: left;
  cursor: pointer;
}

.timeline-item:hover {
  border-color: var(--color-primary);
}

.timeline-item .date,
.timeline-item span:last-child {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.timeline-item strong {
  color: var(--color-heading);
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
  width: min(720px, 100%);
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
  margin: 8px 0;
}

.modal-content time {
  display: block;
  color: var(--color-text-secondary);
  margin-bottom: 14px;
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

@media (max-width: 1024px) {
  .news-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 760px) {
  .sidebar,
  .news-card,
  .timeline-item {
    grid-template-columns: 1fr;
  }

  .section-title {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
