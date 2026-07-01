<script setup>
import { ref } from 'vue'
import { sendContactMessage } from '@/api/contact'
import { officialLinks, openExternal } from '@/data/huangzihongfan'

const form = ref({ name: '', email: '', subject: '', message: '' })
const submitted = ref(false)
const submitting = ref(false)
const errorMsg = ref('')

async function handleSubmit() {
  submitting.value = true
  errorMsg.value = ''
  submitted.value = false
  try {
    await sendContactMessage({ ...form.value })
    submitted.value = true
    form.value = { name: '', email: '', subject: '', message: '' }
  } catch (error) {
    errorMsg.value = '发送失败，请确认后端服务已启动后重试。'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="contact-page">
    <section class="page-hero">
      <span class="eyebrow">CONTACT</span>
      <h1>联系与反馈</h1>
      <p>站内反馈会保存到后端；商务、行程和票务信息请以官方公开平台为准</p>
    </section>

    <main class="container contact-grid">
      <section class="contact-info">
        <h2>公开平台</h2>
        <p class="lead">本站不冒充官方商务渠道，不展示未经核验的邮箱、地址或联系电话。</p>
        <div class="info-list">
          <button @click="openExternal(officialLinks.weibo)">
            <strong>微博主页</strong>
            <span>查看最新官方动态</span>
          </button>
          <button @click="openExternal(officialLinks.damaiSearch)">
            <strong>票务平台搜索</strong>
            <span>核验最新演出售票状态</span>
          </button>
          <button @click="openExternal(officialLinks.qqMusicSearch)">
            <strong>音乐平台搜索</strong>
            <span>收听公开发行作品</span>
          </button>
        </div>
      </section>

      <section class="contact-form">
        <h2>发送站内留言</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <input v-model="form.name" type="text" placeholder="姓名或昵称" required />
            <input v-model="form.email" type="email" placeholder="邮箱" required />
          </div>
          <input v-model="form.subject" type="text" placeholder="主题" required />
          <textarea v-model="form.message" placeholder="留言内容" rows="6" required></textarea>
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? '发送中' : '发送留言' }}
          </button>
        </form>
        <p v-if="submitted" class="success-msg">留言已发送。</p>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      </section>
    </main>
  </div>
</template>

<style scoped>
.contact-page {
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

.contact-grid {
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  gap: 38px;
  padding-top: 56px;
  padding-bottom: 60px;
}

.contact-info,
.contact-form {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
  padding: 26px;
}

.contact-info h2,
.contact-form h2 {
  color: var(--color-heading);
  font-size: 1.35rem;
  margin-bottom: 14px;
}

.lead {
  color: var(--color-text-secondary);
  line-height: 1.75;
  margin-bottom: 20px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-list button {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
  text-align: left;
  cursor: pointer;
}

.info-list button:hover {
  border-color: var(--color-primary);
}

.info-list strong,
.info-list span {
  display: block;
}

.info-list strong {
  color: var(--color-heading);
  margin-bottom: 5px;
}

.info-list span {
  color: var(--color-text-secondary);
  font-size: 0.86rem;
}

.contact-form form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

input,
textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  outline: none;
  font: inherit;
}

input:focus,
textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-pale);
}

textarea {
  resize: vertical;
  min-height: 140px;
}

.success-msg,
.error-msg {
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: var(--radius-md);
}

.success-msg {
  color: #166534;
  background: #f0fdf4;
}

.error-msg {
  color: #991b1b;
  background: #fef2f2;
}

@media (max-width: 780px) {
  .contact-grid,
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
