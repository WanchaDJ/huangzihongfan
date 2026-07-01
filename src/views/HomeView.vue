<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  profile,
  tracks,
  newsItems,
  officialLinks,
  openExternal,
  homeSlides,
  magazineFeatures
} from '@/data/huangzihongfan'

const router = useRouter()

const slides = homeSlides.map((slide) => ({
  ...slide,
  onClick: () => router.push(slide.link)
}))

const currentSlide = ref(0)
let timer = null

function nextSlide() {
  currentSlide.value = (currentSlide.value + 1) % slides.length
}

function goSlide(index) {
  currentSlide.value = index
}

onMounted(() => {
  timer = window.setInterval(nextSlide, 5000)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})

const highlights = [
  {
    mark: '资料',
    title: '人物介绍',
    desc: `${profile.birthplace}出生，${profile.grewUpIn}成长，毕业于${profile.education}。`,
    link: '/about'
  },
  {
    mark: '音乐',
    title: '音乐作品',
    desc: '补齐真实歌曲、专辑曲目和 Apple Music 公开试听片段。',
    link: '/music-album'
  },
  {
    mark: '演出',
    title: '演出活动',
    desc: '只展示已公开、可核验的演出和活动记录，不编造售票场次。',
    link: '/tickets'
  },
  {
    mark: '社区',
    title: '粉丝社区',
    desc: '发帖、评论、点赞、收藏、上传图片和私信都已连到后端。',
    link: '/posts'
  }
]

const featuredTracks = computed(() => tracks.slice(0, 6))
const latestNews = computed(() => newsItems.slice(0, 4))
</script>

<template>
  <div class="home">
    <section class="hero">
      <div class="hero-slider">
        <div
          v-for="(slide, index) in slides"
          :key="slide.id"
          class="hero-slide"
          :class="{ active: index === currentSlide }"
        >
          <img :src="slide.src" :alt="slide.title" loading="eager" decoding="async" />
          <div class="hero-overlay"></div>
          <div class="hero-content">
            <h1>{{ slide.title }}</h1>
            <p>{{ slide.subtitle }}</p>
            <button class="hero-btn" @click="slide.onClick">{{ slide.action }}</button>
          </div>
        </div>
      </div>
      <div class="hero-dots" aria-label="轮播切换">
        <button
          v-for="(_, index) in slides"
          :key="index"
          :class="{ active: index === currentSlide }"
          :aria-label="`切换到第 ${index + 1} 张`"
          @click="goSlide(index)"
        ></button>
      </div>
    </section>

    <section class="features-section">
      <div class="container">
        <div class="features-grid">
          <button v-for="item in highlights" :key="item.title" class="feature-card" @click="router.push(item.link)">
            <span class="feature-mark">{{ item.mark }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </button>
        </div>
      </div>
    </section>

    <section class="songs-section">
      <div class="container">
        <div class="section-header">
          <span class="section-label">MUSIC</span>
          <h2>代表歌曲</h2>
          <p class="section-sub">点击歌曲可进入站内音乐页试听公开预览，或打开音乐平台完整收听。</p>
        </div>
        <div class="songs-grid">
          <button
            v-for="song in featuredTracks"
            :key="song.id"
            class="song-card"
            @click="router.push({ path: '/music-album', query: { track: song.id } })"
          >
            <div class="song-cover">
              <img :src="song.cover" :alt="song.title" loading="lazy" decoding="async" />
              <div class="song-play">{{ song.previewUrl ? '可试听' : '平台收听' }}</div>
            </div>
            <h4>{{ song.title }}</h4>
            <p>{{ song.album }} · {{ song.year }}</p>
          </button>
        </div>
      </div>
    </section>

    <section class="magazine-section">
      <div class="container">
        <div class="section-header">
          <span class="section-label">EDITORIAL</span>
          <h2>杂志与公开写真</h2>
          <p class="section-sub">原先的“晚一点绿灯”轮播展示已替换为杂志和公开视觉内容。</p>
        </div>
        <div class="magazine-grid">
          <button
            v-for="item in magazineFeatures"
            :key="item.id"
            class="magazine-card"
            @click="router.push(item.link)"
          >
            <img :src="item.cover" :alt="item.title" />
            <div class="magazine-body">
              <h3>{{ item.title }}</h3>
              <p>{{ item.summary }}</p>
            </div>
          </button>
        </div>
      </div>
    </section>

    <section class="news-section">
      <div class="container">
        <div class="section-header">
          <span class="section-label">NEWS</span>
          <h2>近期动态</h2>
          <p class="section-sub">按公开资料整理，过期演出会标注为历史记录</p>
        </div>
        <div class="news-list">
          <button
            v-for="item in latestNews"
            :key="item.id"
            class="news-item"
            @click="item.sourceUrl ? openExternal(item.sourceUrl) : router.push('/news')"
          >
            <span class="news-tag">{{ item.category }}</span>
            <span class="news-date">{{ item.date }}</span>
            <h4>{{ item.title }}</h4>
          </button>
        </div>
        <div class="section-cta">
          <button class="btn-primary" @click="router.push('/news')">查看全部动态</button>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container cta-layout">
        <div>
          <span class="section-label">OFFICIAL</span>
          <h2>关注公开平台</h2>
          <p>实时行程、售票状态和最新公开内容请以官方平台、音乐平台与票务平台为准。</p>
        </div>
        <div class="cta-actions">
          <button class="btn-primary" @click="openExternal(officialLinks.weibo)">微博主页</button>
          <button class="btn-outline" @click="openExternal(officialLinks.xiaohongshu)">小红书主页</button>
          <button class="btn-outline" @click="openExternal(officialLinks.douyin)">抖音主页</button>
          <button class="btn-outline" @click="openExternal(officialLinks.damaiSearch)">大麦搜索</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  background: var(--color-background);
  min-height: 100vh;
}

.hero {
  position: relative;
  height: min(74vh, 720px);
  min-height: 500px;
  overflow: hidden;
}

.hero-slider,
.hero-slide {
  height: 100%;
}

.hero-slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.8s ease;
}

.hero-slide.active {
  opacity: 1;
}

.hero-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.24), rgba(0, 0, 0, 0.7));
}

.hero-content {
  position: absolute;
  left: 50%;
  bottom: 15%;
  transform: translateX(-50%);
  width: min(92%, 760px);
  text-align: center;
  color: #fff;
}

.hero-content h1 {
  font-size: clamp(2rem, 4vw, 3.5rem);
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: 0;
  color: #f7d979;
}

.hero-content p {
  font-size: clamp(1rem, 1.8vw, 1.25rem);
  margin-bottom: 28px;
  opacity: 0.92;
}

.hero-btn {
  padding: 12px 34px;
  background: var(--color-primary);
  color: var(--color-heading);
  border: 0;
  border-radius: var(--radius-md);
  font-weight: 700;
  cursor: pointer;
}

.hero-btn:hover {
  background: #fff;
  transform: translateY(-2px);
}

.hero-dots {
  position: absolute;
  left: 50%;
  bottom: 28px;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.hero-dots button {
  width: 10px;
  height: 10px;
  padding: 0;
  border: 2px solid rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.hero-dots button.active {
  background: #fff;
}

.features-section,
.songs-section,
.magazine-section,
.news-section,
.cta-section {
  padding: 64px 0;
}

.songs-section,
.magazine-section {
  background: var(--color-background-soft);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.feature-card,
.song-card,
.news-item,
.magazine-card {
  border: 1px solid var(--color-border);
  background: #fff;
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
}

.feature-card {
  text-align: left;
  padding: 28px 22px;
  border-radius: var(--radius-lg);
}

.feature-card:hover,
.song-card:hover,
.news-item:hover,
.magazine-card:hover {
  transform: translateY(-4px);
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.feature-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 28px;
  padding: 0 10px;
  margin-bottom: 16px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
  font-size: 0.78rem;
  font-weight: 700;
}

.feature-card h3,
.song-card h4,
.news-item h4,
.magazine-card h3 {
  color: var(--color-heading);
}

.feature-card h3 {
  margin-bottom: 8px;
  font-size: 1.08rem;
}

.feature-card p,
.song-card p,
.magazine-card p {
  color: var(--color-text-secondary);
  line-height: 1.65;
  font-size: 0.86rem;
}

.section-label {
  color: var(--color-primary-dark);
  font-weight: 800;
  font-size: 0.75rem;
  letter-spacing: 0.12em;
}

.section-sub {
  color: var(--color-text-secondary);
  margin-top: 8px;
}

.songs-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
}

.song-card {
  padding: 0 0 16px;
  text-align: left;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.song-cover {
  position: relative;
  aspect-ratio: 1;
  margin-bottom: 14px;
  overflow: hidden;
}

.song-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.song-card:hover img {
  transform: scale(1.06);
}

.song-play {
  position: absolute;
  inset: auto 10px 10px 10px;
  padding: 7px 10px;
  border-radius: var(--radius-sm);
  background: rgba(0, 0, 0, 0.68);
  color: #fff;
  font-size: 0.78rem;
  text-align: center;
}

.song-card h4,
.song-card p {
  padding: 0 14px;
}

.song-card h4 {
  font-size: 0.95rem;
  margin-bottom: 5px;
}

.magazine-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.magazine-card {
  overflow: hidden;
  border-radius: var(--radius-lg);
  text-align: left;
}

.magazine-card img {
  width: 100%;
  aspect-ratio: 4 / 5;
  object-fit: cover;
}

.magazine-body {
  padding: 18px;
}

.magazine-body h3 {
  font-size: 1.02rem;
  margin-bottom: 8px;
}

.news-list {
  max-width: 820px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.news-item {
  width: 100%;
  display: grid;
  grid-template-columns: 86px 110px 1fr;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  border-radius: var(--radius-md);
  text-align: left;
}

.news-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
  font-size: 0.74rem;
  font-weight: 700;
}

.news-date {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.news-item h4 {
  font-size: 0.95rem;
  font-weight: 600;
}

.section-cta {
  margin-top: 30px;
  text-align: center;
}

.cta-section {
  background: #1a1815;
  color: rgba(255, 255, 255, 0.75);
}

.cta-layout {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

.cta-layout h2 {
  margin: 8px 0 10px;
  color: #f7d979;
  font-size: 1.8rem;
}

.cta-layout p {
  max-width: 620px;
  line-height: 1.7;
}

.cta-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 1000px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .songs-grid,
  .magazine-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .cta-layout {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .hero {
    height: 62vh;
    min-height: 390px;
  }

  .features-grid,
  .songs-grid,
  .magazine-grid {
    grid-template-columns: 1fr 1fr;
  }

  .news-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style>
