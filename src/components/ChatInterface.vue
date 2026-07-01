<template>
  <div class="chat-interface">
    <!-- 历史记录面板 -->
    <div class="history-panel" :class="{ expanded: showHistoryPanel }">
      <HistoryPanel 
        @toggle="toggleHistoryPanel"
        @select-conversation="selectConversation"
        @delete-conversation="deleteConversation"
      />
    </div>

    <!-- 对话区域 -->
    <div class="chat-main">
      <!-- 对话头部 -->
      <div class="chat-header">
        <button class="history-toggle-btn" @click="toggleHistoryPanel">
          📋
        </button>
        <div class="chat-avatar-container">
          <div class="chat-avatar">
            <div class="avatar-face">
              <div class="avatar-eye avatar-eye-left"></div>
              <div class="avatar-eye avatar-eye-right"></div>
              <div class="avatar-mouth"></div>
            </div>
            <div class="avatar-hair"></div>
          </div>
        </div>
        <h2 class="chat-title">黄子弘凡</h2>
        <div class="chat-status">
          <span 
            class="status-indicator online"
            title="黄子弘凡在线中"
          ></span>
          <span class="status-text">在线中 ✨</span>
        </div>
        <div class="chat-actions">
          <button class="action-btn" @click="createNewConversation" title="新对话">
            🆕
          </button>
          <button class="action-btn" @click="clearCurrentConversation" title="清空对话">
            🗑️
          </button>
          <button class="action-btn" @click="exportConversation" title="导出对话">
            💾
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <MessageList 
        :messages="chatStore.currentMessages"
        @message-click="handleMessageClick"
      />

      <!-- 输入区域 -->
      <InputArea 
        @send="handleSendMessage"
        @upload-file="handleUploadFile"
        :is-sending="chatStore.isSending"
      />

      <!-- 加载状态 -->
      <div v-if="chatStore.loading" class="loading-overlay">
        <div class="loading-spinner"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useAIChatStore } from '../stores/aiChat.js'
import MessageList from './MessageList.vue'
import InputArea from './InputArea.vue'
import HistoryPanel from './HistoryPanel.vue'

const chatStore = useAIChatStore()
const showHistoryPanel = ref(true)

// 切换历史记录面板
const toggleHistoryPanel = () => {
  showHistoryPanel.value = !showHistoryPanel.value
}

// 创建新对话
const createNewConversation = () => {
  chatStore.createNewConversation()
}

// 切换到指定对话
const selectConversation = (conversationId) => {
  chatStore.switchConversation(conversationId)
  // 在移动端或小屏幕上，切换后自动收起历史面板
  if (window.innerWidth < 768) {
    showHistoryPanel.value = false
  }
}

// 删除对话
const deleteConversation = (conversationId) => {
  if (confirm('确定要删除这个对话吗？')) {
    chatStore.deleteConversation(conversationId)
  }
}

// 清空当前对话
const clearCurrentConversation = () => {
  if (confirm('确定要清空当前对话吗？')) {
    chatStore.clearCurrentConversation()
  }
}

// 导出对话
const exportConversation = () => {
  chatStore.exportConversation()
}

// 处理发送消息
const handleSendMessage = (content, files = []) => {
  if (!content.trim() && files.length === 0) return

  // 通过 store 发送消息（使用 HTTP API）
  chatStore.sendMessage(content, files)
}

// 处理上传文件
const handleUploadFile = async (file) => {
  const uploadedFile = await chatStore.uploadFile(file)
  if (uploadedFile) {
    // 可以在这里添加文件上传后的处理逻辑
    console.log('文件上传成功:', uploadedFile)
  }
}

// 处理消息点击
const handleMessageClick = (message) => {
  console.log('点击了消息:', message)
  // 可以在这里添加消息点击的处理逻辑
}

// 监听窗口大小变化，在移动端自动收起历史面板
const handleResize = () => {
  if (window.innerWidth < 768) {
    showHistoryPanel.value = false
  } else {
    showHistoryPanel.value = true
  }
}

// 组件挂载时的初始化
onMounted(() => {
  // 初始化AI聊天存储（加载历史对话）
  chatStore.init()

  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)
  handleResize() // 初始调用一次
})

// 组件卸载时清理资源
onBeforeUnmount(() => {
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.chat-interface {
  display: flex;
  height: calc(100vh - 30px);
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(30px) saturate(180%);
  -webkit-backdrop-filter: blur(30px) saturate(180%);
  color: var(--color-text-primary);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  max-width: 1600px;
  margin: 0 auto;
  border-radius: 28px;
  box-shadow: 
    0 35px 100px rgba(0, 0, 0, 0.18),
    0 15px 40px rgba(255, 107, 107, 0.15),
    0 0 100px rgba(255, 185, 0, 0.1),
    inset 0 2px 0 rgba(255, 255, 255, 0.8);
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;
  transform: perspective(1000px) rotateX(0.5deg);
  transition: transform 0.5s ease;
}

.chat-interface:hover {
  transform: perspective(1000px) rotateX(0deg);
}

/* 历史记录面板 */
.history-panel {
  width: 320px;
  background: linear-gradient(180deg, rgba(255, 248, 230, 0.95) 0%, rgba(255, 240, 210, 0.9) 100%);
  border-right: 2px solid rgba(255, 185, 0, 0.15);
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  overflow-y: auto;
  position: relative;
}

.history-panel::after {
  content: '';
  position: absolute;
  top: 10%;
  right: 0;
  width: 2px;
  height: 80%;
  background: linear-gradient(180deg, transparent, #ffb900, #ff6b6b, transparent);
  opacity: 0.6;
  z-index: 10;
}

/* 历史记录面板滚动条 */
.history-panel::-webkit-scrollbar {
  width: 8px;
}

.history-panel::-webkit-scrollbar-track {
  background: rgba(255, 185, 0, 0.05);
  border-radius: 4px;
  margin: 8px 0;
}

.history-panel::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #ffb900, #ff6b6b);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.history-panel::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #ff8c00, #c44569);
  transform: scale(1.2);
}

.history-panel:not(.expanded) {
  transform: translateX(-100%);
}

/* 对话主区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.5) 0%, rgba(255, 248, 240, 0.3) 100%);
}

/* 对话头部 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  background: linear-gradient(90deg, rgba(255, 185, 0, 0.1) 0%, rgba(255, 255, 255, 0.95) 50%, rgba(255, 107, 107, 0.1) 100%);
  border-bottom: 2px solid rgba(255, 185, 0, 0.3);
  box-shadow: 0 4px 20px rgba(255, 185, 0, 0.1);
  position: relative;
}

.chat-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 10%;
  right: 10%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffb900, #ff6b6b, transparent);
  border-radius: 2px;
}

.history-toggle-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  margin-right: 1rem;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.history-toggle-btn:hover {
  background-color: var(--color-border);
  transform: scale(1.1);
}

/* 聊天头像 */
.chat-avatar-container {
  margin-right: 1rem;
  position: relative;
  transform-style: preserve-3d;
}

.chat-avatar-container::before {
  content: '';
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  background: conic-gradient(from 0deg, #ffb900, #ff6b6b, #c44569, #ff85c0, #ffb900);
  animation: avatarSpin 6s linear infinite, avatarPulse 2s ease-in-out infinite;
  z-index: -1;
  filter: blur(12px);
  opacity: 0.8;
}

.chat-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffdf7e 0%, #ffb347 25%, #ff6b6b 50%, #ff85c0 75%, #ffdf7e 100%);
  background-size: 200% 200%;
  animation: avatarGradient 4s ease infinite, avatar-bounce 2s infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 
    0 8px 30px rgba(255, 185, 0, 0.5),
    0 0 30px rgba(255, 107, 107, 0.3),
    inset 0 2px 10px rgba(255, 255, 255, 0.8);
  border: 4px solid #fff;
  transform-style: preserve-3d;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-avatar:hover {
  transform: scale(1.15) rotateY(15deg);
  box-shadow: 
    0 15px 50px rgba(255, 185, 0, 0.6),
    0 0 50px rgba(255, 107, 107, 0.4),
    0 0 80px rgba(255, 133, 192, 0.3),
    inset 0 2px 10px rgba(255, 255, 255, 0.8);
}

@keyframes rainbowGlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes avatarGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes avatarSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes avatarPulse {
  0%, 100% { 
    transform: scale(1);
    opacity: 0.6;
  }
  50% { 
    transform: scale(1.1);
    opacity: 1;
  }
}

.avatar-face {
  position: relative;
  width: 35px;
  height: 35px;
}

.avatar-eye {
  position: absolute;
  width: 6px;
  height: 6px;
  background-color: #333;
  border-radius: 50%;
  top: 10px;
}

.avatar-eye-left {
  left: 8px;
  animation: blink 3s infinite;
}

.avatar-eye-right {
  right: 8px;
  animation: blink 3s infinite 0.2s;
}

.avatar-mouth {
  position: absolute;
  width: 12px;
  height: 6px;
  background-color: #333;
  border-radius: 0 0 12px 12px;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  animation: mouth-smile 3s infinite;
}

.avatar-hair {
  position: absolute;
  top: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 55px;
  height: 30px;
  background-color: #333;
  border-radius: 50% 50% 0 0;
}

.chat-title {
  flex: 1;
  margin: 0;
  font-size: 1.9rem;
  font-weight: 900;
  color: #d4a63a;
  letter-spacing: 2.5px;
  text-shadow: 0 4px 20px rgba(212, 166, 58, 0.35), 0 2px 10px rgba(80, 52, 0, 0.18);
  position: relative;
  transform-style: preserve-3d;
  transition: all 0.3s ease;
}

.chat-title:hover {
  transform: scale(1.02) translateZ(10px);
  letter-spacing: 3px;
}

/* 在线状态 */
.chat-status {
  margin-right: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-indicator {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #ff4d4f;
  transition: background-color 0.3s ease;
  position: relative;
}

.status-indicator.online {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 0 10px rgba(82, 196, 26, 0.6);
  animation: statusPulse 2s infinite;
}

.status-indicator.online::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  background: rgba(82, 196, 26, 0.3);
  animation: statusRipple 2s infinite;
}

.status-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #52c41a;
  text-shadow: 0 0 10px rgba(82, 196, 26, 0.4);
}

@keyframes statusPulse {
  0%, 100% { 
    transform: scale(1);
    box-shadow: 0 0 10px rgba(82, 196, 26, 0.6);
  }
  50% { 
    transform: scale(1.1);
    box-shadow: 0 0 15px rgba(82, 196, 26, 0.8);
  }
}

@keyframes statusRipple {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(1.8);
    opacity: 0;
  }
}

@keyframes avatar-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

@keyframes blink {
  0%, 90%, 100% { height: 6px; }
  95% { height: 2px; }
}

@keyframes mouth-smile {
  0%, 100% { height: 6px; border-radius: 0 0 12px 12px; }
  50% { height: 8px; border-radius: 50%; }
}

.chat-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  background: linear-gradient(135deg, rgba(255, 185, 0, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  border: 2px solid rgba(255, 185, 0, 0.3);
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.6rem;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #ff8c00;
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:hover {
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 100%);
  color: #fff;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(255, 185, 0, 0.4);
  border-color: transparent;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid var(--color-border);
  border-top: 5px solid var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .history-panel {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
    width: 280px;
  }
  
  .chat-header {
    padding: 0.8rem 1rem;
  }
  
  .chat-title {
    font-size: 1.2rem;
  }
  
  .chat-avatar {
    width: 40px;
    height: 40px;
  }
  
  .avatar-face {
    width: 28px;
    height: 28px;
  }
  
  .avatar-hair {
    width: 45px;
    height: 25px;
  }
  
  .history-toggle-btn {
    margin-right: 0.5rem;
  }
}
</style>
