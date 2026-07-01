<template>
  <div class="auth-page" :style="pageStyle">
    <div class="auth-backdrop"></div>

    <div class="auth-shell">
      <section class="auth-showcase">
        <RouterLink class="brand-link" to="/">黄子弘凡站内专区</RouterLink>

        <div class="showcase-copy">
          <span class="eyebrow">HUANG ZIHONGFAN</span>
          <h1>登录后继续浏览站内内容与互动功能</h1>
          <p>
            会员商品、专辑页面、相册上传、论坛发帖、订单记录与私信入口统一从这里进入。
          </p>
        </div>

        <div class="scene-tags">
          <span v-for="tag in sceneTags" :key="tag">{{ tag }}</span>
        </div>

        <ul class="feature-list">
          <li v-for="item in featureList" :key="item.title">
            <strong>{{ item.title }}</strong>
            <span>{{ item.description }}</span>
          </li>
        </ul>

        <div class="showcase-links">
          <RouterLink to="/">返回首页</RouterLink>
          <RouterLink to="/gallery">查看相册</RouterLink>
          <RouterLink to="/contact">联系与反馈</RouterLink>
        </div>
      </section>

      <section class="auth-panel">
        <div class="panel-top">
          <div>
            <span class="panel-kicker">WELCOME BACK</span>
            <h2>账号登录</h2>
          </div>
          <RouterLink :to="{ path: '/register', query: switchQuery }" class="switch-link">
            注册新账号
          </RouterLink>
        </div>

        <LoginForm v-model:login-fail-count="loginFailCount" />

        <p class="panel-note">
          如果忘记密码，可前往联系页提交找回需求；系统会保留你当前要访问的跳转地址。
        </p>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LoginForm from '../components/common/LoginForm.vue'
import { useAuthStore } from '../stores/auth.js'
import authBg from '@/assets/login-register-bg.jpg'

const LOGIN_FAIL_STORAGE_KEY = 'auth_login_fail_count'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sceneTags = ['会员中心', '歌曲页面', '论坛私信', '相册上传', '订单记录']
const featureList = [
  {
    title: '账号直达原页面',
    description: '登录成功后会回到你刚才想访问的页面，不再停留在登录页。'
  },
  {
    title: '保留常用账号',
    description: '勾选记住账号后，下次登录会自动填入上一次使用的账号。'
  },
  {
    title: '多次失败本地校验',
    description: '连续输错 3 次后会出现本地安全校验，避免重复尝试造成体验混乱。'
  }
]

const pageStyle = computed(() => ({
  '--auth-bg-image': `url(${authBg})`
}))

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/'
})
const switchQuery = computed(() => (
  redirectTarget.value === '/'
    ? {}
    : { redirect: redirectTarget.value }
))

const loginFailCount = ref(readLoginFailCount())

if (authStore.isAuthenticated) {
  router.replace(redirectTarget.value)
}

watch(loginFailCount, (value) => {
  if (value > 0) {
    localStorage.setItem(LOGIN_FAIL_STORAGE_KEY, String(value))
  } else {
    localStorage.removeItem(LOGIN_FAIL_STORAGE_KEY)
  }
}, { immediate: true })

function readLoginFailCount() {
  const raw = Number.parseInt(localStorage.getItem(LOGIN_FAIL_STORAGE_KEY) || '0', 10)
  return Number.isFinite(raw) && raw > 0 ? raw : 0
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  padding: 36px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top left, rgba(201, 168, 76, 0.22), transparent 34%),
    radial-gradient(circle at bottom right, rgba(255, 255, 255, 0.08), transparent 28%),
    #0f0c08;
  overflow: hidden;
}

.auth-backdrop {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(115deg, rgba(13, 10, 7, 0.46), rgba(82, 42, 30, 0.18)),
    var(--auth-bg-image) center/cover no-repeat;
  transform: scale(1.04);
}

.auth-shell {
  position: relative;
  z-index: 1;
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  border: 1px solid rgba(255, 232, 168, 0.18);
  border-radius: 22px;
  overflow: hidden;
  background: rgba(13, 11, 8, 0.24);
  backdrop-filter: blur(16px);
  box-shadow: var(--shadow-xl);
}

.auth-showcase {
  padding: 42px 42px 38px;
  color: #fff7df;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 28px;
  background: linear-gradient(180deg, rgba(18, 14, 9, 0.18), rgba(18, 14, 9, 0.34));
}

.brand-link {
  align-self: flex-start;
  padding: 8px 14px;
  border: 1px solid rgba(255, 232, 168, 0.2);
  border-radius: 999px;
  color: #fff4cf;
  font-size: 0.82rem;
  font-weight: 700;
  background: rgba(255, 255, 255, 0.06);
}

.showcase-copy {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.eyebrow {
  color: #f6d56f;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.1em;
}

.showcase-copy h1 {
  margin: 0;
  color: #f6d56f;
  font-size: clamp(2rem, 3vw, 3.2rem);
  line-height: 1.15;
  text-shadow: 0 2px 14px rgba(0, 0, 0, 0.34);
}

.showcase-copy p {
  max-width: 520px;
  color: rgba(255, 248, 230, 0.88);
  font-size: 1rem;
  line-height: 1.85;
}

.scene-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.scene-tags span {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid rgba(255, 232, 168, 0.2);
  color: #fff4cf;
  font-size: 0.84rem;
  background: rgba(255, 255, 255, 0.06);
}

.feature-list {
  display: grid;
  gap: 16px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  padding-top: 16px;
  border-top: 1px solid rgba(255, 240, 196, 0.16);
}

.feature-list strong,
.feature-list span {
  display: block;
}

.feature-list strong {
  margin-bottom: 6px;
  color: #fff2c0;
  font-size: 0.98rem;
  font-weight: 700;
}

.feature-list span {
  color: rgba(255, 248, 230, 0.8);
  line-height: 1.75;
}

.showcase-links {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}

.showcase-links a {
  color: #f6d56f;
  font-weight: 700;
}

.auth-panel {
  padding: 34px;
  background: rgba(255, 252, 246, 0.94);
}

.panel-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 26px;
}

.panel-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: var(--color-primary-dark);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.panel-top h2 {
  margin: 0;
  color: var(--color-heading);
  font-size: 1.95rem;
}

.switch-link {
  padding: 9px 14px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  color: var(--color-primary-dark);
  font-size: 0.86rem;
  font-weight: 700;
  white-space: nowrap;
}

.panel-note {
  margin-top: 18px;
  color: var(--color-text-secondary);
  font-size: 0.84rem;
  line-height: 1.7;
}

@media (max-width: 980px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-showcase,
  .auth-panel {
    padding: 28px 24px;
  }
}

@media (max-width: 640px) {
  .auth-page {
    padding: 20px 12px;
  }

  .panel-top {
    flex-direction: column;
    align-items: stretch;
  }

  .switch-link {
    text-align: center;
  }
}
</style>
