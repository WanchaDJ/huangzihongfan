<template>
  <div class="security-settings">
    <h2>安全设置</h2>

    <section class="security-card">
      <div>
        <h3>登录密码</h3>
        <p>修改密码会调用后端真实接口，需要输入当前密码。</p>
      </div>
      <button @click="showPasswordModal = true">修改密码</button>
    </section>

    <section class="security-card">
      <div>
        <h3>登录保护</h3>
        <p>开启后，本地会提示你在公共设备退出登录。</p>
      </div>
      <label class="toggle-switch">
        <input v-model="loginProtection" type="checkbox" />
        <span class="slider"></span>
      </label>
    </section>

    <section class="security-card">
      <div>
        <h3>账号信息</h3>
        <p>当前登录用户：{{ auth.user?.username || '用户' }}</p>
      </div>
      <button @click="auth.fetchCurrentUser()">刷新登录状态</button>
    </section>

    <p v-if="message" class="message">{{ message }}</p>

    <div v-if="showPasswordModal" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>修改密码</h3>
        <form @submit.prevent="changePassword">
          <label>
            当前密码
            <input v-model="form.oldPassword" type="password" required autocomplete="current-password" />
          </label>
          <label>
            新密码
            <input v-model="form.newPassword" type="password" minlength="8" required autocomplete="new-password" />
          </label>
          <label>
            确认新密码
            <input v-model="form.checkPassword" type="password" minlength="8" required autocomplete="new-password" />
          </label>
          <div class="form-actions">
            <button type="button" class="secondary" @click="closeModal">取消</button>
            <button type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { updatePassword } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const showPasswordModal = ref(false)
const loginProtection = ref(true)
const saving = ref(false)
const message = ref('')

const form = reactive({
  oldPassword: '',
  newPassword: '',
  checkPassword: ''
})

function closeModal() {
  showPasswordModal.value = false
  form.oldPassword = ''
  form.newPassword = ''
  form.checkPassword = ''
}

async function changePassword() {
  message.value = ''
  if (form.newPassword !== form.checkPassword) {
    message.value = '两次输入的新密码不一致。'
    return
  }
  saving.value = true
  try {
    await updatePassword({ ...form })
    message.value = '密码已修改。'
    closeModal()
  } catch (error) {
    message.value = error.message || '密码修改失败。'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.security-settings h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
  margin-bottom: 20px;
}

.security-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 18px;
  margin-bottom: 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.security-card h3 {
  color: var(--color-heading);
  margin-bottom: 6px;
}

.security-card p {
  color: var(--color-text-secondary);
}

.security-card button,
.form-actions button {
  padding: 9px 14px;
  border: 0;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
  cursor: pointer;
}

.message {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  inset: 0;
  cursor: pointer;
  background: #ccc;
  transition: 0.25s;
  border-radius: 26px;
}

.slider::before {
  position: absolute;
  content: "";
  width: 20px;
  height: 20px;
  left: 3px;
  bottom: 3px;
  background: white;
  transition: 0.25s;
  border-radius: 50%;
}

input:checked + .slider {
  background: var(--color-primary);
}

input:checked + .slider::before {
  transform: translateX(24px);
}

.modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.55);
}

.modal-content {
  width: min(430px, 100%);
  padding: 24px;
  border-radius: var(--radius-lg);
  background: #fff;
}

.modal-content h3 {
  color: var(--color-heading);
  margin-bottom: 16px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: var(--color-heading);
  font-weight: 700;
}

input {
  width: 100%;
  padding: 11px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.form-actions .secondary {
  background: var(--color-background-soft);
  color: var(--color-text);
}

@media (max-width: 620px) {
  .security-card {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
