<template>
  <div class="input-area">
    <!-- 上传的文件预览 -->
    <div v-if="uploadedFiles.length > 0" class="file-preview-list">
      <div 
        v-for="(file, index) in uploadedFiles" 
        :key="index" 
        class="file-preview-item"
      >
        <span class="file-preview-icon">📄</span>
        <span class="file-preview-name">{{ file.name }}</span>
        <span class="file-preview-size">({{ formatFileSize(file.size) }})</span>
        <button class="file-preview-remove" @click="removeFile(index)">×</button>
      </div>
    </div>
    
    <!-- 输入框和操作按钮 -->
    <div class="input-container">
      <!-- 文件上传按钮 -->
      <label class="file-upload-btn" @click="triggerFileInput">
        📎
        <input 
          ref="fileInput" 
          type="file" 
          multiple 
          accept=".txt,.pdf" 
          @change="handleFileSelect"
          style="display: none;"
        />
      </label>
      
      <!-- 文本输入框 -->
      <textarea 
        ref="inputTextarea" 
        v-model="messageText" 
        class="message-input" 
        placeholder="请输入您的问题... (Enter发送，Shift+Enter换行)" 
        @keydown="handleKeyDown"
        :disabled="isSending"
        :maxlength="5000"
      ></textarea>
      
      <!-- 发送按钮 -->
      <button 
        class="send-btn" 
        @click="sendMessage" 
        :disabled="!canSend || isSending"
      >
        <span v-if="isSending" class="send-spinner"></span>
        <span v-else>发送</span>
      </button>
    </div>
    
    <!-- 字符计数 -->
    <div class="char-count" v-if="messageText.length > 0">
      {{ messageText.length }}/5000
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Props
const props = defineProps({
  isSending: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['send', 'upload-file'])

// Refs
const inputTextarea = ref(null)
const fileInput = ref(null)
const messageText = ref('')
const uploadedFiles = ref([])

// 计算属性：是否可以发送消息
const canSend = computed(() => {
  return (messageText.value.trim() !== '' || uploadedFiles.value.length > 0) && !props.isSending
})

// 触发文件选择对话框
const triggerFileInput = () => {
  if (props.isSending) return
  fileInput.value?.click()
}

// 处理文件选择
const handleFileSelect = () => {
  if (!fileInput.value.files || fileInput.value.files.length === 0) return
  
  const files = Array.from(fileInput.value.files)
  
  // 过滤文件类型
  const allowedTypes = [
    'text/plain',
    'application/pdf',
    '.txt',
    '.pdf'
  ]
  
  const validFiles = files.filter(file => {
    const isAllowedType = allowedTypes.includes(file.type) || 
                         allowedTypes.includes(`.${file.name.split('.').pop().toLowerCase()}`)
    
    if (!isAllowedType) {
      alert(`文件 ${file.name} 类型不支持，仅支持 TXT 和 PDF 格式`)
    }
    
    return isAllowedType
  })
  
  // 添加到上传文件列表
  uploadedFiles.value = [...uploadedFiles.value, ...validFiles]
  
  // 清空文件输入
  fileInput.value.value = ''
  
  // 触发文件上传事件
  validFiles.forEach(file => {
    emit('upload-file', file)
  })
}

// 移除已选择的文件
const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

// 处理键盘事件
const handleKeyDown = (event) => {
  // Enter发送消息
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
  // 最多允许5000个字符
  if (messageText.value.length > 5000) {
    messageText.value = messageText.value.substring(0, 5000)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!canSend.value) return
  
  // 获取消息内容和文件
  const content = messageText.value.trim()
  const files = [...uploadedFiles.value]
  
  // 清空输入和文件列表
  messageText.value = ''
  uploadedFiles.value = []
  
  // 聚焦输入框
  inputTextarea.value?.focus()
  
  // 发送消息
  emit('send', content, files)
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  
  const units = ['B', 'KB', 'MB', 'GB']
  let unitIndex = 0
  let currentSize = size
  
  while (currentSize >= 1024 && unitIndex < units.length - 1) {
    currentSize /= 1024
    unitIndex++
  }
  
  return `${currentSize.toFixed(2)} ${units[unitIndex]}`
}
</script>

<style scoped>
.input-area {
  padding: 1.2rem 1.5rem;
  background: linear-gradient(180deg, transparent 0%, rgba(255, 248, 240, 0.5) 100%);
  border-top: 2px solid rgba(255, 185, 0, 0.2);
  position: relative;
}

.input-area::before {
  content: '';
  position: absolute;
  top: 0;
  left: 15%;
  right: 15%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffb900, #ff6b6b, transparent);
}

.file-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
  margin-bottom: 1.2rem;
}

.file-preview-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.7rem 1rem;
  background: linear-gradient(135deg, rgba(255, 185, 0, 0.1) 0%, rgba(255, 107, 107, 0.08) 100%);
  border: 2px solid rgba(255, 185, 0, 0.2);
  border-radius: 24px;
  font-size: 0.9rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.file-preview-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  transition: left 0.5s;
}

.file-preview-item:hover::before {
  left: 100%;
}

.file-preview-item:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 20px rgba(255, 185, 0, 0.2);
  border-color: rgba(255, 185, 0, 0.4);
}

.file-preview-icon {
  font-size: 1.2rem;
  filter: drop-shadow(0 2px 4px rgba(255, 185, 0, 0.3));
}

.file-preview-name {
  font-weight: 600;
  color: #444;
}

.file-preview-size {
  color: #888;
  font-size: 0.8rem;
}

.file-preview-remove {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2) 0%, rgba(196, 69, 105, 0.2) 100%);
  border: none;
  color: #ff6b6b;
  cursor: pointer;
  font-size: 1.3rem;
  line-height: 1;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
  font-weight: bold;
}

.file-preview-remove:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #c44569 100%);
  color: white;
  transform: scale(1.15) rotate(90deg);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 0.8rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 28px;
  padding: 0.6rem;
  box-shadow: 0 8px 30px rgba(255, 185, 0, 0.15), inset 0 2px 10px rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(255, 185, 0, 0.2);
  backdrop-filter: blur(10px);
}

.file-upload-btn {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 185, 0, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.3rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(255, 185, 0, 0.3);
}

.file-upload-btn:hover {
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 100%);
  color: #fff;
  transform: scale(1.1) rotate(15deg);
  box-shadow: 0 5px 20px rgba(255, 185, 0, 0.4);
  border-color: transparent;
}

.message-input {
  flex: 1;
  min-height: 42px;
  max-height: 150px;
  padding: 0.7rem 1.2rem;
  border: none;
  outline: none;
  font-size: 1rem;
  resize: none;
  line-height: 1.6;
  background: transparent;
  color: #333;
}

.message-input::placeholder {
  color: #aaa;
}

.send-btn {
  width: 90px;
  height: 42px;
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 50%, #c44569 100%);
  background-size: 200% 100%;
  color: white;
  border: none;
  border-radius: 21px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 5px 20px rgba(255, 185, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.send-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.send-btn:hover:not(:disabled)::before {
  left: 100%;
}

.send-btn:hover:not(:disabled) {
  background-position: 100% 0;
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 30px rgba(255, 185, 0, 0.4);
}

.send-btn:disabled {
  background: linear-gradient(135deg, #ccc 0%, #bbb 100%);
  cursor: not-allowed;
  box-shadow: none;
}

.send-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-area {
    padding: 0.8rem;
  }
  
  .file-upload-btn {
    width: 32px;
    height: 32px;
    font-size: 1rem;
  }
  
  .message-input {
    font-size: 0.9rem;
    padding: 0.4rem 0.8rem;
  }
  
  .send-btn {
    width: 70px;
    height: 32px;
    font-size: 0.9rem;
  }
}
</style>