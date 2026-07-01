import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser, updateMyUser, userLogin, userLogout as apiLogout, userRegister } from '../api/user'
import { resolveAvatar } from '@/utils/avatar'

function normalizeUser(raw, fallbackAccount = '') {
  if (!raw) return null
  const userName = raw.userName || raw.username || fallbackAccount || '用户'
  return {
    id: raw.id,
    userName,
    username: userName,
    userAccount: raw.userAccount || fallbackAccount || '',
    email: raw.userAccount || fallbackAccount || '',
    avatar: resolveAvatar(raw, userName),
    userAvatar: raw.userAvatar || raw.avatar || '',
    userProfile: raw.userProfile || '',
    userRole: raw.userRole || 'user',
    createTime: raw.createTime || '',
    updateTime: raw.updateTime || ''
  }
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref('')

  const isAuthenticated = computed(() => !!user.value?.id)

  function loadFromStorage() {
    token.value = localStorage.getItem('auth_token') || ''
    const storedUser = localStorage.getItem('auth_user')
    if (storedUser) {
      try {
        user.value = normalizeUser(JSON.parse(storedUser))
      } catch {
        user.value = null
      }
    }
  }

  function saveToStorage() {
    if (user.value) {
      localStorage.setItem('auth_user', JSON.stringify(user.value))
      localStorage.setItem('auth_token', token.value || `session_${user.value.id}`)
    } else {
      localStorage.removeItem('auth_user')
      localStorage.removeItem('auth_token')
    }
  }

  async function login(userAccount, userPassword) {
    const res = await userLogin({ userAccount, userPassword })
    user.value = normalizeUser(res.data, userAccount)
    token.value = `session_${user.value.id}_${Date.now()}`
    saveToStorage()
    return user.value
  }

  async function register(userAccount, userPassword, checkPassword, userName) {
    await userRegister({ userAccount, userPassword, checkPassword, userName })
    return login(userAccount, userPassword)
  }

  async function fetchCurrentUser() {
    const res = await getLoginUser()
    user.value = normalizeUser(res.data)
    token.value = `session_${user.value.id}`
    saveToStorage()
    return user.value
  }

  async function saveUser(updates) {
    const payload = {}
    if (updates.userName || updates.username) payload.userName = updates.userName || updates.username
    if (updates.userAvatar || updates.avatar) payload.userAvatar = updates.userAvatar || updates.avatar
    if (updates.userProfile !== undefined) payload.userProfile = updates.userProfile
    await updateMyUser(payload)
    user.value = normalizeUser({ ...user.value, ...payload })
    saveToStorage()
    return user.value
  }

  function updateProfile(updates) {
    user.value = normalizeUser({ ...user.value, ...updates })
    saveToStorage()
    return user.value
  }

  async function logout() {
    try {
      await apiLogout()
    } catch {
      // session may already be invalid
    }
    user.value = null
    token.value = ''
    saveToStorage()
  }

  loadFromStorage()

  return {
    user,
    token,
    isAuthenticated,
    login,
    register,
    fetchCurrentUser,
    saveUser,
    updateProfile,
    logout
  }
})
