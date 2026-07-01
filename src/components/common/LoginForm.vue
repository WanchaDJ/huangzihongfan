<template>
  <div class="login-form">
    <div class="form-header">
      <span class="form-kicker">SIGN IN</span>
      <h2>账号登录</h2>
      <p>当前支持站内账号密码登录，登录后可继续访问会员中心、相册上传、论坛互动和订单记录。</p>
    </div>

    <div v-if="showSecurityCheck" class="security-banner">
      连续输错 3 次后，需要先完成本地安全校验。
    </div>

    <form class="form" @submit.prevent="handleSubmit">
      <div class="field-group">
        <label for="login-account">账号</label>
        <input
          id="login-account"
          v-model="form.account"
          type="text"
          maxlength="16"
          autocomplete="username"
          placeholder="请输入 4-16 位字母或数字账号"
          @input="clearError('account')"
        />
        <p v-if="errors.account" class="error-text">{{ errors.account }}</p>
      </div>

      <div class="field-group">
        <div class="field-label-row">
          <label for="login-password">密码</label>
          <button type="button" class="inline-link" @click="goToContact">忘记密码</button>
        </div>
        <div class="password-wrap">
          <input
            id="login-password"
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="current-password"
            placeholder="请输入密码"
            @input="clearError('password')"
          />
          <button type="button" class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
      </div>

      <div v-if="showSecurityCheck" class="field-group">
        <div class="field-label-row">
          <label for="login-security-code">安全校验</label>
          <button type="button" class="inline-link" @click="refreshSecurityCode">换一张</button>
        </div>
        <div class="security-row">
          <input
            id="login-security-code"
            v-model="form.securityCode"
            type="text"
            maxlength="4"
            autocomplete="off"
            placeholder="输入右侧字符"
            @input="clearError('securityCode')"
          />
          <button type="button" class="security-code" @click="refreshSecurityCode">
            {{ securityCode }}
          </button>
        </div>
        <p class="field-hint">该校验仅用于减少重复尝试，不会提交到后端。</p>
        <p v-if="errors.securityCode" class="error-text">{{ errors.securityCode }}</p>
      </div>

      <div class="form-extra">
        <label class="remember-row">
          <input v-model="form.remember" type="checkbox" />
          <span>记住账号</span>
        </label>
        <span class="helper-text">首次登录后会自动保存当前会话</span>
      </div>

      <button type="submit" class="submit-button" :disabled="submitting">
        <span v-if="submitting" class="loading-dot"></span>
        {{ submitting ? '登录中' : '登录' }}
      </button>

      <p class="switch-row">
        还没有账号？
        <RouterLink :to="{ path: '/register', query: redirectQuery }">立即注册</RouterLink>
      </p>
    </form>

    <p v-if="globalError" class="global-error">{{ globalError }}</p>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'

const props = defineProps({
  loginFailCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['login-success', 'login-fail', 'update:loginFailCount'])

const LAST_ACCOUNT_STORAGE_KEY = 'auth_last_account'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const rememberedAccount = localStorage.getItem(LAST_ACCOUNT_STORAGE_KEY) || ''

const form = reactive({
  account: rememberedAccount,
  password: '',
  remember: Boolean(rememberedAccount),
  securityCode: ''
})

const errors = reactive({
  account: '',
  password: '',
  securityCode: ''
})

const showPassword = ref(false)
const submitting = ref(false)
const globalError = ref('')
const securityCode = ref('')

const showSecurityCheck = computed(() => props.loginFailCount >= 3)
const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/'
})
const redirectQuery = computed(() => (
  redirectTarget.value === '/'
    ? {}
    : { redirect: redirectTarget.value }
))

watch(showSecurityCheck, (visible) => {
  if (visible) {
    refreshSecurityCode()
  } else {
    form.securityCode = ''
    errors.securityCode = ''
  }
}, { immediate: true })

function createSecurityCode() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  return Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
}

function refreshSecurityCode() {
  securityCode.value = createSecurityCode()
  form.securityCode = ''
  errors.securityCode = ''
}

function clearError(field) {
  errors[field] = ''
  globalError.value = ''
}

function goToContact() {
  router.push('/contact')
}

function validate() {
  const account = form.account.trim()
  let valid = true

  errors.account = ''
  errors.password = ''
  errors.securityCode = ''

  if (!account) {
    errors.account = '请输入账号'
    valid = false
  } else if (account.length < 4 || account.length > 16) {
    errors.account = '账号长度需为 4-16 位'
    valid = false
  } else if (!/^[A-Za-z0-9]+$/.test(account)) {
    errors.account = '账号仅支持字母和数字'
    valid = false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (form.password.length < 8) {
    errors.password = '密码至少需要 8 位'
    valid = false
  }

  if (showSecurityCheck.value) {
    const localCode = form.securityCode.trim().toUpperCase()
    if (!localCode) {
      errors.securityCode = '请输入安全校验码'
      valid = false
    } else if (localCode !== securityCode.value) {
      errors.securityCode = '安全校验码不正确'
      valid = false
    }
  }

  return valid
}

async function handleSubmit() {
  globalError.value = ''

  if (!validate()) {
    return
  }

  const account = form.account.trim()

  submitting.value = true
  try {
    await auth.login(account, form.password)

    if (form.remember) {
      localStorage.setItem(LAST_ACCOUNT_STORAGE_KEY, account)
    } else {
      localStorage.removeItem(LAST_ACCOUNT_STORAGE_KEY)
    }

    emit('update:loginFailCount', 0)
    emit('login-success', auth.user)
    router.replace(redirectTarget.value)
  } catch (error) {
    const nextFailCount = props.loginFailCount + 1
    emit('update:loginFailCount', nextFailCount)
    emit('login-fail', error)

    globalError.value = error?.message || '登录失败，请检查账号和密码后重试。'
    form.password = ''

    if (nextFailCount >= 3) {
      refreshSecurityCode()
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
}

.form-header {
  margin-bottom: 26px;
}

.form-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: var(--color-primary-dark);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.form-header h2 {
  margin: 0 0 8px;
  color: var(--color-heading);
  font-size: 1.85rem;
  font-weight: 700;
}

.form-header p {
  color: var(--color-text-secondary);
  line-height: 1.75;
}

.security-banner {
  margin-bottom: 18px;
  padding: 12px 14px;
  border: 1px solid rgba(201, 168, 76, 0.28);
  border-radius: var(--radius-md);
  background: rgba(201, 168, 76, 0.1);
  color: var(--color-primary-text);
  font-size: 0.88rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.field-group label {
  color: var(--color-heading);
  font-size: 0.9rem;
  font-weight: 600;
}

.field-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.field-group input {
  width: 100%;
  min-height: 48px;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.94);
}

.field-group input::placeholder {
  color: var(--color-text-muted);
}

.password-wrap,
.security-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
}

.password-wrap input,
.security-row input {
  min-width: 0;
}

.toggle-password,
.inline-link {
  padding: 0;
  background: transparent;
  color: var(--color-primary-dark);
  font-size: 0.84rem;
  font-weight: 600;
}

.toggle-password {
  min-width: 48px;
}

.security-code {
  min-width: 96px;
  min-height: 48px;
  padding: 0 14px;
  border: 1px solid rgba(201, 168, 76, 0.28);
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(201, 168, 76, 0.15), rgba(255, 255, 255, 0.96));
  color: var(--color-heading);
  font-size: 1rem;
  font-weight: 800;
  letter-spacing: 0.2em;
}

.field-hint {
  color: var(--color-text-secondary);
  font-size: 0.76rem;
}

.error-text {
  color: var(--color-danger);
  font-size: 0.8rem;
}

.form-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.remember-row {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text);
  cursor: pointer;
}

.remember-row input {
  width: 16px;
  height: 16px;
  accent-color: var(--color-primary);
}

.helper-text {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.submit-button {
  min-height: 50px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-hover));
  color: #17120b;
  font-size: 1rem;
  font-weight: 800;
  box-shadow: var(--shadow-gold);
}

.submit-button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.loading-dot {
  display: inline-block;
  width: 15px;
  height: 15px;
  margin-right: 8px;
  border: 2px solid rgba(23, 18, 11, 0.2);
  border-top-color: #17120b;
  border-radius: 999px;
  vertical-align: -2px;
  animation: spin 0.75s linear infinite;
}

.switch-row {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  text-align: center;
}

.switch-row a {
  margin-left: 6px;
  font-weight: 700;
}

.global-error {
  margin-top: 18px;
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--color-danger-pale);
  color: var(--color-danger);
  font-size: 0.86rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .password-wrap,
  .security-row {
    grid-template-columns: 1fr;
  }

  .security-code,
  .toggle-password {
    width: 100%;
  }
}
</style>
