<template>
  <div class="auth-page" :style="pageStyle">
    <div class="auth-backdrop"></div>

    <div class="auth-shell">
      <section class="auth-showcase">
        <RouterLink class="brand-link" to="/">黄子弘凡站内专区</RouterLink>

        <div class="showcase-copy">
          <span class="eyebrow">JOIN THE SITE</span>
          <h1>注册后即可使用站内社区、相册与会员功能</h1>
          <p>
            账号创建完成后会自动登录，你可以继续收藏商品、上传图片、发布帖子并查看个人订单。
          </p>
        </div>

        <div class="scene-tags">
          <span v-for="tag in sceneTags" :key="tag">{{ tag }}</span>
        </div>

        <ol class="step-list">
          <li v-for="step in stepList" :key="step.title">
            <strong>{{ step.title }}</strong>
            <span>{{ step.description }}</span>
          </li>
        </ol>

        <div class="showcase-links">
          <RouterLink to="/">返回首页</RouterLink>
          <RouterLink to="/member-center">会员中心</RouterLink>
          <RouterLink to="/posts">进入论坛</RouterLink>
        </div>
      </section>

      <section class="auth-panel">
        <div class="panel-top">
          <div>
            <span class="panel-kicker">NEW ACCOUNT</span>
            <h2>注册并登录</h2>
          </div>
          <RouterLink :to="{ path: '/login', query: switchQuery }" class="switch-link">
            已有账号
          </RouterLink>
        </div>

        <RegisterForm />

        <p class="panel-note">
          昵称可用于论坛和评论展示；未填写时系统会默认使用你的账号名称。
        </p>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RegisterForm from '../components/common/RegisterForm.vue'
import { useAuthStore } from '../stores/auth.js'
import authBg from '@/assets/login-register-bg.jpg'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sceneTags = ['发布帖子', '上传相册', '收藏商品', '会员权益', '私信互动']
const stepList = [
  {
    title: '创建站内账号',
    description: '使用账号密码完成注册，注册成功后会立即写入当前会话。'
  },
  {
    title: '完善昵称与资料',
    description: '昵称会用于个人主页、论坛发帖和评论区的公开展示。'
  },
  {
    title: '继续刚才的操作',
    description: '如果你是从会员中心或其他受限页面跳转过来，注册后会自动回到原页面。'
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

if (authStore.isAuthenticated) {
  router.replace(redirectTarget.value)
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
    radial-gradient(circle at top right, rgba(201, 168, 76, 0.2), transparent 30%),
    radial-gradient(circle at bottom left, rgba(255, 255, 255, 0.08), transparent 28%),
    #0f0c08;
  overflow: hidden;
}

.auth-backdrop {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(115deg, rgba(11, 10, 8, 0.46), rgba(82, 42, 30, 0.18)),
    var(--auth-bg-image) center/cover no-repeat;
  transform: scale(1.04);
}

.auth-shell {
  position: relative;
  z-index: 1;
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: 1.02fr 0.98fr;
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

.step-list {
  display: grid;
  gap: 16px;
  list-style: none;
  padding: 0;
  margin: 0;
  counter-reset: register-step;
}

.step-list li {
  position: relative;
  padding: 16px 0 0 48px;
  border-top: 1px solid rgba(255, 240, 196, 0.16);
}

.step-list li::before {
  counter-increment: register-step;
  content: counter(register-step);
  position: absolute;
  left: 0;
  top: 14px;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(201, 168, 76, 0.18);
  color: #fff2c0;
  font-weight: 800;
}

.step-list strong,
.step-list span {
  display: block;
}

.step-list strong {
  margin-bottom: 6px;
  color: #fff2c0;
  font-size: 0.98rem;
  font-weight: 700;
}

.step-list span {
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
