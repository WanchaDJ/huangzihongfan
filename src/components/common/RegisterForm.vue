<template>
  <div class="register-form">
    <div class="form-header">
      <span class="form-kicker">CREATE ACCOUNT</span>
      <h2>创建账号</h2>
      <p>注册完成后会自动登录，并跳转到你刚才准备访问的页面。</p>
    </div>

    <form class="form" @submit.prevent="handleSubmit">
      <div class="field-group">
        <label for="register-name">昵称</label>
        <input
          id="register-name"
          v-model="form.userName"
          type="text"
          maxlength="20"
          autocomplete="nickname"
          placeholder="选填，未填写时默认使用账号"
          @input="clearError('userName')"
        />
        <p class="field-hint">用于个人中心、论坛和评论展示。</p>
        <p v-if="errors.userName" class="error-text">{{ errors.userName }}</p>
      </div>

      <div class="field-group">
        <label for="register-account">账号</label>
        <input
          id="register-account"
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
        <label for="register-password">密码</label>
        <div class="password-wrap">
          <input
            id="register-password"
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="new-password"
            placeholder="至少 8 位，建议同时包含字母和数字"
            @input="clearError('password')"
          />
          <button type="button" class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        <div v-if="form.password" class="strength-row">
          <div class="strength-bars">
            <span
              v-for="bar in 4"
              :key="bar"
              :class="['strength-bar', strengthBarClass(bar)]"
            ></span>
          </div>
          <span :class="['strength-label', strengthTone]">{{ strengthLabel }}</span>
        </div>
        <p class="field-hint">后端校验规则为至少 8 位；当前页面不会额外限制特殊字符。</p>
        <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
      </div>

      <div class="field-group">
        <label for="register-confirm-password">确认密码</label>
        <input
          id="register-confirm-password"
          v-model="form.confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          autocomplete="new-password"
          placeholder="请再次输入密码"
          @input="clearError('confirmPassword')"
        />
        <p v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</p>
      </div>

      <button type="submit" class="submit-button" :disabled="submitting">
        <span v-if="submitting" class="loading-dot"></span>
        {{ submitting ? '注册中' : '注册并登录' }}
      </button>

      <p class="switch-row">
        已有账号？
        <RouterLink :to="{ path: '/login', query: redirectQuery }">立即登录</RouterLink>
      </p>
    </form>

    <p v-if="globalError" class="global-error">{{ globalError }}</p>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'

const emit = defineEmits(['register-success', 'register-fail'])

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({
  userName: '',
  account: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  userName: '',
  account: '',
  password: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const submitting = ref(false)
const globalError = ref('')

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/'
})
const redirectQuery = computed(() => (
  redirectTarget.value === '/'
    ? {}
    : { redirect: redirectTarget.value }
))

const strengthScore = computed(() => {
  let score = 0
  const password = form.password

  if (password.length >= 8) score += 1
  if (/[A-Za-z]/.test(password) && /\d/.test(password)) score += 1
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score += 1
  if (/[^A-Za-z0-9]/.test(password) || password.length >= 12) score += 1

  return score
})

const strengthLabel = computed(() => {
  if (!form.password) return ''
  if (form.password.length < 8) return '长度不足'
  if (strengthScore.value <= 1) return '基础'
  if (strengthScore.value === 2) return '较稳妥'
  if (strengthScore.value === 3) return '强'
  return '很强'
})

const strengthTone = computed(() => {
  if (!form.password || form.password.length < 8) return 'weak'
  if (strengthScore.value <= 1) return 'weak'
  if (strengthScore.value === 2) return 'medium'
  return 'strong'
})

function strengthBarClass(bar) {
  if (!form.password || form.password.length < 8) {
    return ''
  }
  if (bar <= strengthScore.value) {
    return strengthTone.value
  }
  return ''
}

function clearError(field) {
  errors[field] = ''
  globalError.value = ''
}

function validate() {
  const account = form.account.trim()
  const userName = form.userName.trim()
  let valid = true

  errors.userName = ''
  errors.account = ''
  errors.password = ''
  errors.confirmPassword = ''

  if (userName && (userName.length < 2 || userName.length > 20)) {
    errors.userName = '昵称长度需为 2-20 个字符'
    valid = false
  }

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

  if (!form.confirmPassword) {
    errors.confirmPassword = '请再次输入密码'
    valid = false
  } else if (form.confirmPassword !== form.password) {
    errors.confirmPassword = '两次输入的密码不一致'
    valid = false
  }

  return valid
}

async function handleSubmit() {
  globalError.value = ''

  if (!validate()) {
    return
  }

  submitting.value = true
  try {
    const account = form.account.trim()
    const userName = form.userName.trim() || account

    await auth.register(account, form.password, form.confirmPassword, userName)
    emit('register-success', auth.user)
    router.replace(redirectTarget.value)
  } catch (error) {
    globalError.value = error?.message || '注册失败，请稍后重试。'
    emit('register-fail', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.register-form {
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

.field-hint {
  color: var(--color-text-secondary);
  font-size: 0.76rem;
}

.password-wrap {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
}

.toggle-password {
  min-width: 48px;
  padding: 0;
  background: transparent;
  color: var(--color-primary-dark);
  font-size: 0.84rem;
  font-weight: 600;
}

.strength-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.strength-bars {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 6px;
  flex: 1;
}

.strength-bar {
  height: 5px;
  border-radius: 999px;
  background: var(--color-border);
}

.strength-bar.weak {
  background: #f59e0b;
}

.strength-bar.medium {
  background: var(--color-primary);
}

.strength-bar.strong {
  background: var(--color-success);
}

.strength-label {
  min-width: 52px;
  font-size: 0.8rem;
  font-weight: 700;
  text-align: right;
}

.strength-label.weak {
  color: #b45309;
}

.strength-label.medium {
  color: var(--color-primary-dark);
}

.strength-label.strong {
  color: var(--color-success);
}

.error-text {
  color: var(--color-danger);
  font-size: 0.8rem;
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
  .password-wrap {
    grid-template-columns: 1fr;
  }

  .toggle-password {
    width: 100%;
  }

  .strength-row {
    flex-direction: column;
    align-items: stretch;
  }

  .strength-label {
    text-align: left;
  }
}
</style>
