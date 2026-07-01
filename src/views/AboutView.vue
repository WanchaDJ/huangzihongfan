<script setup>
import { profile, milestones, galleryPhotos, sourceNotes, openExternal } from '@/data/huangzihongfan'

const infoItems = [
  ['姓名', profile.name],
  ['英文名', profile.englishName],
  ['出生日期', '1999-04-21'],
  ['出生地', profile.birthplace],
  ['成长地', profile.grewUpIn],
  ['毕业院校', profile.education],
  ['职业', profile.occupations.join('、')],
  ['出道关注', '《声入人心》第一季']
]
</script>

<template>
  <div class="about-page">
    <section class="about-hero">
      <div class="container hero-layout">
        <div>
          <span class="eyebrow">PROFILE</span>
          <h1>{{ profile.name }}</h1>
          <p>{{ profile.occupations.join(' / ') }}</p>
        </div>
        <button class="btn-outline hero-source" @click="openExternal(sourceNotes[0].url)">查看公开资料来源</button>
      </div>
    </section>

    <section class="container about-bio">
      <div class="bio-image">
        <img :src="profile.avatar" :alt="profile.name" />
      </div>
      <div class="bio-content">
        <div class="bio-block">
          <h2>基本信息</h2>
          <div class="info-grid">
            <div v-for="[label, value] in infoItems" :key="label" class="info-item">
              <span class="label">{{ label }}</span>
              <span class="value">{{ value }}</span>
            </div>
          </div>
          <p class="data-note">{{ profile.publicDataNote }}</p>
        </div>

        <div class="bio-block">
          <h2>个人简介</h2>
          <p v-for="text in profile.intro" :key="text">{{ text }}</p>
        </div>
      </div>
    </section>

    <section class="career-section">
      <div class="container">
        <div class="section-header">
          <span class="eyebrow">TIMELINE</span>
          <h2>演艺经历</h2>
        </div>
        <div class="timeline">
          <article v-for="item in milestones" :key="item.year + item.title" class="tl-item">
            <div class="tl-year">{{ item.year }}</div>
            <div class="tl-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.detail }}</p>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="container photos-section">
      <div class="section-header">
        <span class="eyebrow">GALLERY</span>
        <h2>公开图片</h2>
      </div>
      <div class="photos-grid">
        <img v-for="photo in galleryPhotos.slice(0, 6)" :key="photo.caption" :src="photo.src" :alt="photo.caption" />
      </div>
    </section>

    <section class="container source-section">
      <h2>资料来源</h2>
      <p>本站人物、音乐与演出信息按公开资料整理；实时行程与售票请以官方平台为准。</p>
      <div class="source-list">
        <button v-for="source in sourceNotes" :key="source.name" @click="openExternal(source.url)">
          {{ source.name }}
        </button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.about-page {
  background: var(--color-background);
  min-height: 100vh;
}

.about-hero {
  background: linear-gradient(135deg, #1a1815 0%, #2d2418 50%, #19152c 100%);
  padding: 64px 0;
  color: #fff;
}

.hero-layout {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
}

.eyebrow {
  color: var(--color-primary);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.about-hero h1 {
  margin: 8px 0;
  font-size: clamp(2.2rem, 5vw, 4rem);
  font-weight: 800;
}

.about-hero p {
  color: rgba(255, 255, 255, 0.72);
  font-size: 1.05rem;
}

.hero-source {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.45);
}

.about-bio {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 48px;
  padding-top: 56px;
  padding-bottom: 56px;
}

.bio-image img {
  width: 100%;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.bio-block {
  margin-bottom: 34px;
}

.bio-block h2,
.career-section h2,
.photos-section h2,
.source-section h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
  margin-bottom: 18px;
}

.bio-block p,
.source-section p {
  color: var(--color-text);
  line-height: 1.85;
  margin-bottom: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
}

.label {
  display: block;
  margin-bottom: 4px;
  color: var(--color-text-secondary);
  font-size: 0.76rem;
}

.value {
  color: var(--color-heading);
  font-weight: 700;
  font-size: 0.95rem;
}

.data-note {
  margin-top: 12px;
  color: var(--color-text-secondary);
  font-size: 0.86rem;
}

.career-section {
  padding: 56px 0;
  background: var(--color-background-soft);
}

.section-header {
  text-align: center;
  margin-bottom: 32px;
}

.timeline {
  max-width: 820px;
  margin: 0 auto;
  position: relative;
}

.tl-item {
  display: grid;
  grid-template-columns: 90px 1fr;
  gap: 18px;
  margin-bottom: 18px;
}

.tl-year {
  color: var(--color-primary-dark);
  font-weight: 800;
  font-size: 1rem;
  padding-top: 18px;
  text-align: right;
}

.tl-content {
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 18px 20px;
}

.tl-content h3 {
  color: var(--color-heading);
  font-size: 1rem;
  margin-bottom: 6px;
}

.tl-content p {
  color: var(--color-text-secondary);
  line-height: 1.65;
  font-size: 0.9rem;
}

.photos-section,
.source-section {
  padding-top: 56px;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.photos-grid img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.source-section {
  padding-bottom: 64px;
}

.source-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.source-list button {
  padding: 9px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  color: var(--color-heading);
  cursor: pointer;
}

.source-list button:hover {
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
}

@media (max-width: 768px) {
  .hero-layout,
  .about-bio {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: flex-start;
  }

  .bio-image {
    max-width: 280px;
  }

  .info-grid,
  .photos-grid {
    grid-template-columns: 1fr;
  }

  .tl-item {
    grid-template-columns: 1fr;
  }

  .tl-year {
    text-align: left;
    padding-top: 0;
  }
}
</style>
