<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { onMounted, onUnmounted, ref } from 'vue'
import NavigationBar from './components/common/NavigationBar.vue'
import { sendContactMessage } from '@/api/contact'
import { officialLinks, openExternal } from '@/data/huangzihongfan'

const isScrolled = ref(false)
const showBackToTop = ref(false)
const subscribeEmail = ref('')
const footerMessage = ref('')
const policyModal = ref('')

function handleScroll() {
  isScrolled.value = window.scrollY > 50
  showBackToTop.value = window.scrollY > 400
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function subscribeNews() {
  footerMessage.value = ''
  if (!subscribeEmail.value.trim()) {
    footerMessage.value = '请输入邮箱地址。'
    return
  }
  try {
    await sendContactMessage({
      name: '动态订阅',
      email: subscribeEmail.value.trim(),
      subject: '黄子弘凡网站动态订阅',
      message: '请向我发送新的公开动态、音乐作品和演出提醒。'
    })
    footerMessage.value = '订阅已提交。'
    subscribeEmail.value = ''
  } catch {
    footerMessage.value = '订阅失败，请确认后端服务已启动。'
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="app-container">
    <NavigationBar :is-scrolled="isScrolled" />

    <main class="main-content">
      <RouterView v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </RouterView>
    </main>

    <footer class="site-footer">
      <div class="footer-container">
        <div class="footer-grid">
          <div class="footer-brand">
            <h3>黄子弘凡资料站</h3>
            <p>公开资料、音乐作品、演出活动与粉丝社区。实时行程和票务以官方平台为准。</p>
            <div class="footer-social">
              <button @click="openExternal(officialLinks.weibo)">微博</button>
              <button @click="openExternal(officialLinks.appleMusic)">Apple Music</button>
              <button @click="openExternal(officialLinks.damaiSearch)">票务</button>
            </div>
          </div>
          <div class="footer-links">
            <h4>站内导航</h4>
            <RouterLink to="/">首页</RouterLink>
            <RouterLink to="/about">关于</RouterLink>
            <RouterLink to="/news">动态</RouterLink>
            <RouterLink to="/gallery">相册</RouterLink>
            <RouterLink to="/tickets">演出</RouterLink>
          </div>
          <div class="footer-links">
            <h4>功能入口</h4>
            <RouterLink to="/music-album">音乐作品</RouterLink>
            <RouterLink to="/posts">粉丝社区</RouterLink>
            <RouterLink to="/member-center">会员中心</RouterLink>
            <RouterLink to="/contact">联系反馈</RouterLink>
          </div>
          <div class="footer-subscribe">
            <h4>订阅公开动态</h4>
            <p>提交邮箱后会写入后端联系消息，用于站内提醒。</p>
            <form class="subscribe-form" @submit.prevent="subscribeNews">
              <input v-model="subscribeEmail" type="email" placeholder="邮箱地址" />
              <button type="submit">订阅</button>
            </form>
            <p v-if="footerMessage" class="footer-message">{{ footerMessage }}</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 黄子弘凡资料站。非官方商业渠道。</p>
          <div class="footer-legal">
            <button @click="policyModal = '隐私说明'">隐私说明</button>
            <button @click="policyModal = '服务说明'">服务说明</button>
          </div>
        </div>
      </div>
    </footer>

    <button class="back-to-top" :class="{ visible: showBackToTop }" @click="scrollToTop" aria-label="回到顶部">
      ↑
    </button>

    <div v-if="policyModal" class="policy-modal" @click="policyModal = ''">
      <div class="policy-box" @click.stop>
        <button class="policy-close" @click="policyModal = ''">×</button>
        <h2>{{ policyModal }}</h2>
        <p>
          本站用于课程或个人项目展示，收集的留言和订阅邮箱仅写入本项目后端数据库。
          站内不售卖未核验票务，不代表黄子弘凡本人、工作室或任何官方商务渠道。
        </p>
      </div>
    </div>
  </div>
</template>

<style>
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.25s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  width: 100%;
  margin-top: 60px;
}

.site-footer {
  margin-top: 80px;
  padding: 56px 0 0;
  background: #1a1815;
  color: rgba(255, 255, 255, 0.66);
}

.footer-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.8fr 1fr 1fr 1.5fr;
  gap: 40px;
  padding-bottom: 40px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.footer-brand h3,
.footer-links h4,
.footer-subscribe h4 {
  color: #fff;
  margin-bottom: 14px;
}

.footer-brand h3 {
  color: var(--color-primary);
  font-size: 1.35rem;
}

.footer-brand p,
.footer-subscribe p {
  line-height: 1.65;
  font-size: 0.88rem;
}

.footer-social {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.footer-social button,
.footer-legal button {
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.78);
  cursor: pointer;
}

.footer-social button {
  padding: 8px 12px;
}

.footer-social button:hover,
.footer-legal button:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.footer-links a {
  display: block;
  padding: 5px 0;
  color: rgba(255, 255, 255, 0.52);
  text-decoration: none;
  font-size: 0.88rem;
}

.footer-links a:hover {
  color: var(--color-primary);
}

.subscribe-form {
  display: flex;
  gap: 8px;
  margin-top: 14px;
}

.subscribe-form input {
  flex: 1;
  min-width: 0;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  outline: none;
}

.subscribe-form button {
  padding: 10px 18px;
  border: 0;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
  cursor: pointer;
}

.footer-message {
  margin-top: 10px;
  color: var(--color-primary);
}

.footer-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 20px 0;
  font-size: 0.82rem;
}

.footer-legal {
  display: flex;
  gap: 10px;
}

.footer-legal button {
  padding: 7px 10px;
}

.back-to-top {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 99;
  width: 44px;
  height: 44px;
  border: 0;
  border-radius: 50%;
  background: var(--color-primary);
  color: #17130e;
  font-size: 1.25rem;
  font-weight: 900;
  cursor: pointer;
  opacity: 0;
  visibility: hidden;
  transform: translateY(8px);
  transition: all 0.25s ease;
}

.back-to-top.visible {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.policy-modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.6);
}

.policy-box {
  width: min(520px, 100%);
  position: relative;
  padding: 28px;
  border-radius: var(--radius-lg);
  background: #fff;
}

.policy-box h2 {
  color: var(--color-heading);
  margin-bottom: 10px;
}

.policy-box p {
  color: var(--color-text-secondary);
  line-height: 1.75;
}

.policy-close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 50%;
  background: var(--color-background-soft);
  cursor: pointer;
  font-size: 1.3rem;
}

@media (max-width: 900px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 600px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .footer-bottom {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
