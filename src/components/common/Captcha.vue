<template>
  <div class="captcha-container">
    <div class="captcha-input-group">
      <input
        type="text"
        v-model="captchaValue"
        :placeholder="placeholder"
        :disabled="isLoading"
        class="captcha-input"
      />
      <div class="captcha-image-container">
        <img
          :src="captchaUrl"
          :alt="placeholder"
          class="captcha-image"
          @click="refreshCaptcha"
        />
        <div v-if="isLoading" class="captcha-loading">
          <div class="spinner"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import axios from 'axios'

const props = defineProps({
  placeholder: {
    type: String,
    default: '请输入验证码'
  },
  captchaKey: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:captchaValue', 'captchaLoaded'])

const captchaUrl = ref('')
const captchaValue = ref('')
const captchaId = ref('')
const isLoading = ref(false)

// 生成随机验证码URL（模拟）
const generateCaptchaUrl = () => {
  // 实际项目中这里应该调用后端API生成验证码
  // 这里使用随机数模拟不同的验证码
  return `https://picsum.photos/seed/${Date.now()}/120/40`
}

// 刷新验证码
const refreshCaptcha = async () => {
  isLoading.value = true
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    captchaUrl.value = generateCaptchaUrl()
    captchaId.value = Date.now().toString()
    captchaValue.value = ''
    emit('update:captchaValue', captchaValue.value)
  } catch (error) {
    console.error('刷新验证码失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 当验证码值变化时通知父组件
watchEffect(() => {
  emit('update:captchaValue', captchaValue.value)
})

// 组件挂载时生成初始验证码
onMounted(() => {
  refreshCaptcha()
})

// 导出方法供父组件使用
defineExpose({
  refreshCaptcha,
  getCaptchaId: () => captchaId.value
})
</script>

<style scoped>
.captcha-container {
  width: 100%;
}

.captcha-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 0.95rem;
  transition: border-color 0.3s;
}

.captcha-input:focus {
  outline: none;
  border-color: #ffb900;
}

.captcha-image-container {
  position: relative;
  width: 120px;
  height: 40px;
}

.captcha-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #e0e0e0;
}

.captcha-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #ffb900;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>