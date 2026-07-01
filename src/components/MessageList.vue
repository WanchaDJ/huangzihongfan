<template>
  <div class="message-list" ref="chatContainer" @scroll="handleScroll">
    <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-icon">
              <div class="welcome-avatar">
                <div class="avatar-face">
                  <div class="avatar-eye avatar-eye-left"></div>
                  <div class="avatar-eye avatar-eye-right"></div>
                  <div class="avatar-mouth"></div>
                </div>
              </div>
            </div>
            <h2>嗨！我是黄子弘凡～</h2>
            <p>终于等到你啦！快来和我聊聊天吧～ 可以聊音乐、音乐剧、美食、旅行，或者随便什么话题都可以哦！我超会聊天的！😉🎵</p>
          </div>
    
    <div v-else class="virtual-scroll-container">
      <!-- 占位元素，用于撑开容器高度 -->
      <div 
        class="virtual-scroll-placeholder" 
        :style="{ height: totalHeight + 'px' }"
      ></div>
      
      <!-- 可见消息列表 -->
      <div 
        class="virtual-scroll-content" 
        :style="{ transform: `translateY(${startOffset}px)` }"
      >
        <div 
          v-for="message in visibleMessages" 
          :key="message.id" 
          class="message-item" 
          :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
          @click="handleMessageClick(message)"
        >
          <div class="message-content">
            <div class="message-text" v-html="renderMarkdown(message.content)"></div>
            
            <!-- 文件列表 -->
            <div v-if="message.files && message.files.length > 0" class="message-files">
              <div 
                v-for="(file, index) in message.files" 
                :key="index" 
                class="message-file-item"
                @click.stop="downloadFile(file)"
              >
                <span class="file-icon">📄</span>
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">({{ formatFileSize(file.size) }})</span>
              </div>
            </div>
            
            <!-- 打字指示器 -->
            <div v-if="message.generating && message.type === 'ai'" class="typing-indicator">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
            </div>
            
            <!-- 时间戳 -->
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 滚动到底部按钮 -->
    <button 
      v-if="showScrollToBottomBtn && messages.length > 0" 
      class="scroll-to-bottom-btn" 
      @click="scrollToBottom"
    >
      ↓
    </button>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted, nextTick } from 'vue'
import { useAIChatStore } from '../stores/aiChat.js'
// 导入markdown渲染库
import { marked } from 'marked'
import DOMPurify from 'dompurify'

// Props
const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  showAvatar: {
    type: Boolean,
    default: true
  },
  showTimestamp: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits(['message-click'])

// Refs
const chatContainer = ref(null)
const showScrollToBottomBtn = ref(false)
let isAutoScrolling = true

// 虚拟滚动相关变量
const itemHeight = 80 // 估算的平均消息高度
const bufferItems = 5 // 可见区域外的缓冲消息数量
const containerHeight = ref(0)
const currentScrollTop = ref(0)

// 计算总高度
const totalHeight = computed(() => {
  return props.messages.length * itemHeight
})

// 计算可见消息的起始和结束索引
const visibleRange = computed(() => {
  if (!chatContainer.value) return { start: 0, end: props.messages.length }
  
  const start = Math.max(0, Math.floor(currentScrollTop.value / itemHeight) - bufferItems)
  const end = Math.min(
    props.messages.length,
    Math.ceil((currentScrollTop.value + containerHeight.value) / itemHeight) + bufferItems
  )
  
  return { start, end }
})

// 计算可见消息列表
const visibleMessages = computed(() => {
  const { start, end } = visibleRange.value
  return props.messages.slice(start, end)
})

// 计算内容偏移量
const startOffset = computed(() => {
  const { start } = visibleRange.value
  return start * itemHeight
})

// 监听滚动事件
const handleScroll = (e) => {
  currentScrollTop.value = e.target.scrollTop
  
  if (!chatContainer.value) return
  
  const containerScrollTop = chatContainer.value.scrollTop
  const { scrollHeight, clientHeight } = chatContainer.value
  const isNearBottom = scrollHeight - containerScrollTop - clientHeight < 200
  
  showScrollToBottomBtn.value = !isNearBottom
  isAutoScrolling = isNearBottom
}

// 滚动到聊天底部
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    showScrollToBottomBtn.value = false
  }
}

// 监听消息列表变化，自动滚动到底部
watch(
  () => props.messages.length,
  async () => {
    if (isAutoScrolling) {
      await nextTick()
      scrollToBottom()
    }
  }
)

// 监听消息内容变化，自动滚动到底部（用于AI回复打字机效果）
watch(
  () => props.messages,
  async () => {
    // 检查最后一条消息是否是AI的且正在生成中
    const lastMessage = props.messages[props.messages.length - 1]
    if (lastMessage && lastMessage.type === 'ai') {
      await nextTick()
      scrollToBottom()
    }
  },
  { deep: true }
)

// 组件挂载时滚动到底部
onMounted(() => {
  if (chatContainer.value) {
    containerHeight.value = chatContainer.value.clientHeight
    scrollToBottom()
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    if (chatContainer.value) {
      containerHeight.value = chatContainer.value.clientHeight
    }
  })
})

// 处理消息点击事件
const handleMessageClick = (message) => {
  emit('message-click', message)
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 渲染Markdown内容，防止XSS攻击
const renderMarkdown = (content) => {
  if (!content) return ''
  
  try {
    // 使用marked库将Markdown转换为HTML
    const rawHtml = marked(content)
    // 使用DOMPurify过滤恶意代码
    return DOMPurify.sanitize(rawHtml)
  } catch (error) {
    console.error('渲染Markdown失败:', error)
    return content // 失败时返回原始内容
  }
}

// 下载文件
const downloadFile = (file) => {
  if (!file.url) return
  
  // 创建下载链接
  const link = document.createElement('a')
  link.href = file.url
  link.download = file.name || 'download.file'
  
  // 模拟点击下载
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<style scoped>
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  background: transparent;
  position: relative;
}

.virtual-scroll-container {
  position: relative;
  width: 100%;
}

.virtual-scroll-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  pointer-events: none;
}

.virtual-scroll-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
}

.welcome-message {
  text-align: center;
  margin: auto;
  color: var(--color-text-secondary);
  padding: 3rem 2rem;
}

.welcome-icon {
  margin-bottom: 2rem;
  position: relative;
  display: inline-block;
}

.welcome-icon::before {
  content: '';
  position: absolute;
  top: -15px;
  left: -15px;
  right: -15px;
  bottom: -15px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ffb900, #ff6b6b, #c44569, #ffb900);
  background-size: 300% 300%;
  animation: rainbowGlow 4s ease infinite;
  z-index: -1;
  filter: blur(15px);
  opacity: 0.5;
}

.welcome-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffdf7e 0%, #ffb347 50%, #ffeb99 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 15px 40px rgba(255, 185, 0, 0.4), inset 0 3px 15px rgba(255, 255, 255, 0.6);
  margin: 0 auto 1rem;
  animation: avatar-bounce 2.5s infinite;
  border: 4px solid #fff;
}

.avatar-face {
  position: relative;
  width: 70px;
  height: 70px;
}

.avatar-eye {
  position: absolute;
  width: 12px;
  height: 12px;
  background-color: #333;
  border-radius: 50%;
  top: 20px;
}

.avatar-eye-left {
  left: 16px;
  animation: blink 3s infinite;
}

.avatar-eye-right {
  right: 16px;
  animation: blink 3s infinite 0.2s;
}

.avatar-mouth {
  position: absolute;
  width: 24px;
  height: 12px;
  background-color: #333;
  border-radius: 0 0 24px 24px;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  animation: mouth-smile 3s infinite;
}

.welcome-message h2 {
  font-size: 2.5rem;
  margin-bottom: 1.2rem;
  color: #d4a63a;
  font-weight: 900;
  letter-spacing: 3px;
  filter: 
    drop-shadow(0 4px 15px rgba(255, 185, 0, 0.4))
    drop-shadow(0 2px 8px rgba(255, 107, 107, 0.3));
  transform-style: preserve-3d;
  transition: all 0.5s ease;
}

.welcome-message h2:hover {
  transform: scale(1.05) translateZ(20px);
  letter-spacing: 4px;
}

.welcome-message p {
  font-size: 1.25rem;
  color: #555;
  max-width: 580px;
  margin: 0 auto;
  line-height: 2;
  position: relative;
  opacity: 0.9;
  transition: all 0.3s ease;
}

.welcome-message p:hover {
  opacity: 1;
  transform: scale(1.02);
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  max-width: 90%;
  margin-bottom: 1rem;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-message {
  align-self: flex-start;
}

.message-content {
  position: relative;
  padding: 1.2rem 1.5rem;
  border-radius: 22px;
  word-wrap: break-word;
  line-height: 1.7;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(15px);
  transform-style: preserve-3d;
  perspective: 1000px;
}

.message-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.6) 0%, transparent 100%);
  border-radius: 22px 22px 0 0;
  pointer-events: none;
}

.message-content:hover {
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px) scale(1.01);
}

.user-message .message-content {
  background: linear-gradient(135deg, #ffb900 0%, #ffdf7e 25%, #ff8c66 50%, #ff6b6b 75%, #ff8c00 100%);
  background-size: 200% 200%;
  animation: messageGradient 5s ease infinite;
  color: #333;
  border-bottom-right-radius: 8px;
  box-shadow: 
    0 10px 30px rgba(255, 185, 0, 0.35),
    0 0 20px rgba(255, 107, 107, 0.2),
    inset 0 2px 0 rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
}

@keyframes messageGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.ai-message .message-content {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 248, 240, 0.95) 50%, rgba(255, 240, 250, 0.9) 100%);
  color: var(--color-text-primary);
  border-bottom-left-radius: 8px;
  border: 2px solid rgba(255, 185, 0, 0.25);
  box-shadow: 
    0 10px 30px rgba(0, 0, 0, 0.08),
    0 0 20px rgba(255, 185, 0, 0.1),
    inset 0 2px 0 rgba(255, 255, 255, 0.8);
}

.message-text {
  margin-bottom: 0.5rem;
}

.message-text code {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9rem;
}

.user-message .message-text code {
  background-color: rgba(0, 0, 0, 0.2);
  color: white;
}

.message-text a {
  color: inherit;
  text-decoration: underline;
  font-weight: 500;
}

.message-files {
  margin-top: 0.8rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.message-file-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-message .message-file-item {
  background-color: rgba(0, 0, 0, 0.1);
}

.message-file-item:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.user-message .message-file-item:hover {
  background-color: rgba(0, 0, 0, 0.15);
}

.file-size {
  color: var(--color-text-secondary);
  font-size: 0.8rem;
}

.user-message .file-size {
  color: rgba(0, 0, 0, 0.7);
}

.message-time {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  margin-top: 0.5rem;
  text-align: right;
}

.user-message .message-time {
  color: rgba(0, 0, 0, 0.7);
}

.scroll-to-bottom-btn {
  position: fixed;
  bottom: 130px;
  right: 25px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb900 0%, #ffdf7e 25%, #ff6b6b 50%, #ff85c0 75%, #ffb900 100%);
  background-size: 200% 200%;
  animation: buttonGradient 3s ease infinite;
  color: #fff;
  border: 3px solid rgba(255, 255, 255, 0.8);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    0 8px 30px rgba(255, 185, 0, 0.4),
    0 0 20px rgba(255, 107, 107, 0.3),
    inset 0 2px 10px rgba(255, 255, 255, 0.6);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1.4rem;
  font-weight: bold;
  overflow: hidden;
}

.scroll-to-bottom-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  transition: left 0.5s;
}

.scroll-to-bottom-btn:hover::before {
  left: 100%;
}

@keyframes buttonGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.scroll-to-bottom-btn:hover {
  transform: scale(1.15) translateY(-5px);
  box-shadow: 
    0 15px 45px rgba(255, 185, 0, 0.5),
    0 0 40px rgba(255, 107, 107, 0.4),
    0 0 60px rgba(255, 133, 192, 0.3),
    inset 0 2px 10px rgba(255, 255, 255, 0.8);
}

/* 滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 10px;
}

.message-list::-webkit-scrollbar-track {
  background: linear-gradient(180deg, rgba(255, 185, 0, 0.05) 0%, rgba(255, 107, 107, 0.05) 100%);
  border-radius: 5px;
}

.message-list::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #ffb900 0%, #ff6b6b 50%, #c44569 100%);
  border-radius: 5px;
  border: 2px solid transparent;
  background-clip: padding-box;
  transition: all 0.3s ease;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #ff8c00 0%, #ff5252 50%, #a13d58 100%);
  background-clip: padding-box;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, var(--color-primary-dark), var(--color-primary));
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-list {
    padding: 1rem;
  }
  
  .message-item {
    max-width: 95%;
  }
  
  .scroll-to-bottom-btn {
    bottom: 100px;
    right: 15px;
    width: 40px;
    height: 40px;
  }
  
  .welcome-avatar {
    width: 80px;
    height: 80px;
  }
  
  .avatar-face {
    width: 56px;
    height: 56px;
  }
  
  .avatar-eye {
    width: 10px;
    height: 10px;
    top: 16px;
  }
  
  .avatar-mouth {
    width: 20px;
    height: 10px;
  }
}

/* 打字指示器样式 */
.typing-indicator {
  display: flex;
  align-items: center;
  margin-top: 12px;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(255, 185, 0, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  border-radius: 20px;
  display: inline-flex;
}

.typing-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 100%);
  animation: typingBounce 1.4s infinite ease-in-out;
  box-shadow: 0 2px 8px rgba(255, 185, 0, 0.4);
  position: relative;
}

.typing-dot::before {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}

.typing-dot:nth-child(1) {
  animation-delay: -0.4s;
  background: linear-gradient(135deg, #ffb900 0%, #ffdf7e 100%);
}

.typing-dot:nth-child(2) {
  animation-delay: -0.2s;
  background: linear-gradient(135deg, #ff8c66 0%, #ff6b6b 100%);
}

.typing-dot:nth-child(3) {
  background: linear-gradient(135deg, #c44569 0%, #ff85c0 100%);
}

@keyframes typingBounce {
  0%, 80%, 100% {
    transform: translateY(0) scale(0.6);
    opacity: 0.5;
  }
  40% {
    transform: translateY(-8px) scale(1.1);
    opacity: 1;
  }
}

@keyframes avatar-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes blink {
  0%, 90%, 100% { height: 12px; }
  95% { height: 2px; }
}

@keyframes mouth-smile {
  0%, 100% { height: 12px; border-radius: 0 0 24px 24px; }
  50% { height: 16px; border-radius: 50%; }
}
</style>
