<template>
  <div class="profile-editor">
    <div class="section-head">
      <h2>编辑资料</h2>
      <RouterLink class="btn-outline" to="/profile">返回</RouterLink>
    </div>

    <form class="edit-form" @submit.prevent="submitForm">
      <div class="avatar-row">
        <img :src="previewAvatar" alt="用户头像" />
        <div>
          <input ref="avatarInput" type="file" accept="image/*" hidden @change="handleAvatarChange" />
          <button type="button" class="btn-outline" @click="avatarInput?.click()">上传头像</button>
          <p>支持 jpg、png、webp、gif，最大 5MB。</p>
        </div>
      </div>

      <div class="form-grid">
        <label>
          昵称
          <input v-model.trim="form.username" type="text" maxlength="30" required />
        </label>
        <label>
          手机号
          <input v-model.trim="form.phone" type="tel" placeholder="可选" />
        </label>
        <label>
          生日
          <input v-model="form.birthday" type="date" />
        </label>
        <label>
          性别
          <select v-model="form.gender">
            <option :value="null">不公开</option>
            <option :value="1">男</option>
            <option :value="2">女</option>
            <option :value="0">其他</option>
          </select>
        </label>
        <label>
          省份
          <input v-model.trim="form.province" type="text" placeholder="例如：广东省" />
        </label>
        <label>
          城市
          <input v-model.trim="form.city" type="text" placeholder="例如：广州市" />
        </label>
        <label>
          区县
          <input v-model.trim="form.district" type="text" />
        </label>
        <label>
          详细地址
          <input v-model.trim="form.detailAddress" type="text" />
        </label>
        <label class="wide">
          个人简介
          <textarea v-model.trim="form.bio" rows="4" maxlength="300"></textarea>
        </label>
      </div>

      <p v-if="message" class="message">{{ message }}</p>

      <div class="form-actions">
        <button type="button" class="btn-outline" @click="loadUserData">重置</button>
        <button type="submit" class="btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存修改' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemberStore } from '@/stores/member'
import { uploadFile } from '@/api/file'
import { resolveAvatar } from '@/utils/avatar'

const router = useRouter()
const authStore = useAuthStore()
const memberStore = useMemberStore()
const avatarInput = ref(null)
const avatarFile = ref(null)
const localAvatarUrl = ref('')
const saving = ref(false)
const message = ref('')

const form = reactive({
  username: '',
  phone: '',
  birthday: '',
  gender: null,
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  bio: ''
})

const previewAvatar = computed(() => localAvatarUrl.value || resolveAvatar(authStore.user, form.username || '用户'))

function toDateInput(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value).slice(0, 10)
  return date.toISOString().slice(0, 10)
}

async function loadUserData() {
  await memberStore.fetchMemberInfo()
  form.username = authStore.user?.username || ''
  form.phone = memberStore.memberInfo.phone || ''
  form.birthday = toDateInput(memberStore.memberInfo.birthday)
  form.gender = memberStore.memberInfo.gender ?? null
  form.province = memberStore.memberInfo.province || ''
  form.city = memberStore.memberInfo.city || ''
  form.district = memberStore.memberInfo.district || ''
  form.detailAddress = memberStore.memberInfo.detailAddress || ''
  form.bio = memberStore.memberInfo.bio || authStore.user?.userProfile || ''
  avatarFile.value = null
  localAvatarUrl.value = ''
}

function handleAvatarChange(event) {
  const file = event.target.files?.[0]
  if (!file) return
  avatarFile.value = file
  if (localAvatarUrl.value) URL.revokeObjectURL(localAvatarUrl.value)
  localAvatarUrl.value = URL.createObjectURL(file)
}

async function submitForm() {
  saving.value = true
  message.value = ''
  try {
    let avatarUrl = authStore.user?.userAvatar || authStore.user?.avatar
    if (avatarFile.value) {
      const uploadRes = await uploadFile(avatarFile.value, 'user_avatar')
      avatarUrl = uploadRes.data
    }
    await authStore.saveUser({
      userName: form.username,
      userAvatar: avatarUrl,
      userProfile: form.bio
    })
    await memberStore.updateProfile({
      phone: form.phone,
      birthday: form.birthday || null,
      gender: form.gender,
      province: form.province,
      city: form.city,
      district: form.district,
      detailAddress: form.detailAddress,
      bio: form.bio
    })
    message.value = '资料已保存。'
    router.push('/profile')
  } catch (error) {
    message.value = error.message || '保存失败，请稍后重试。'
  } finally {
    saving.value = false
  }
}

onMounted(loadUserData)
</script>

<style scoped>
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
}

.section-head h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.avatar-row img {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-primary);
}

.avatar-row p {
  margin-top: 8px;
  color: var(--color-text-secondary);
  font-size: 0.84rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 7px;
  color: var(--color-heading);
  font-weight: 700;
}

input,
select,
textarea {
  width: 100%;
  padding: 11px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text);
}

textarea {
  resize: vertical;
}

.wide {
  grid-column: 1 / -1;
}

.message {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 680px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .avatar-row {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
