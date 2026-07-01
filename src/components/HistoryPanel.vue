<template>
  <div class="history-panel">
    <!-- 面板头部 -->
    <div class="history-header">
      <h3 class="history-title">对话历史</h3>
      <button class="create-conversation-btn" @click="createNewConversation">
        ➕
      </button>
    </div>
    
    <!-- 历史记录列表 -->
    <div class="history-list">
      <div 
        v-for="conversation in conversations" 
        :key="conversation.id" 
        class="conversation-item" 
        :class="{ 'active': conversation.id === activeConversationId, 'unread': conversation.unread > 0 }"
        @click="selectConversation(conversation.id)"
      >
        <!-- 对话内容预览 -->
        <div class="conversation-content">
          <div class="conversation-title">{{ conversation.title }}</div>
          <div class="conversation-preview">
            {{ getConversationPreview(conversation) }}
          </div>
          <div class="conversation-time">
            {{ formatTime(conversation.updatedAt) }}
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="conversation-actions">
          <!-- 未读标记 -->
          <div v-if="conversation.unread > 0" class="unread-badge">
            {{ conversation.unread }}
          </div>
          
          <!-- 删除按钮 -->
          <button 
            class="delete-conversation-btn" 
            @click.stop="deleteConversation(conversation.id)"
          >
            ×
          </button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="conversations.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <p>暂无对话记录</p>
        <button class="create-first-conversation" @click="createNewConversation">
          创建新对话
        </button>
      </div>
    </div>
    
    <!-- 面板底部 -->
    <div class="history-footer">
      <button class="clear-all-btn" @click="clearAllConversations">
        清空所有对话
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAIChatStore } from '../stores/aiChat.js'

// Emits
const emit = defineEmits(['toggle', 'select-conversation', 'delete-conversation'])

// Store
const chatStore = useAIChatStore()

// 计算属性：获取对话历史
const conversations = computed(() => {
  return chatStore.conversationHistory.sort((a, b) => {
    return new Date(b.updatedAt) - new Date(a.updatedAt)
  })
})

// 计算属性：获取当前活动对话ID
const activeConversationId = computed(() => {
  return chatStore.activeConversationId
})

// 创建新对话
const createNewConversation = () => {
  chatStore.createNewConversation()
  emit('toggle') // 移动端关闭面板
}

// 选择对话
const selectConversation = (conversationId) => {
  emit('select-conversation', conversationId)
}

// 删除对话
const deleteConversation = (conversationId) => {
  emit('delete-conversation', conversationId)
}

// 清空所有对话
const clearAllConversations = () => {
  if (confirm('确定要清空所有对话吗？此操作不可恢复。')) {
    chatStore.clearAllConversations()
  }
}

// 获取对话内容预览
const getConversationPreview = (conversation) => {
  if (!conversation.messages || conversation.messages.length === 0) {
    return '暂无消息'
  }
  
  const lastMessage = conversation.messages[conversation.messages.length - 1]
  let preview = lastMessage.content
  
  // 如果消息太长，截断显示
  if (preview.length > 50) {
    preview = preview.substring(0, 50) + '...'
  }
  
  // 如果有文件，显示文件信息
  if (lastMessage.files && lastMessage.files.length > 0) {
    preview += ` (${lastMessage.files.length}个文件)`
  }
  
  return preview
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    // 今天，显示时间
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (diffDays === 1) {
    // 昨天
    return '昨天'
  } else if (diffDays < 7) {
    // 一周内，显示星期
    const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return days[date.getDay()]
  } else {
    // 超过一周，显示日期
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}
</script>

<style scoped>
.history-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: transparent;
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.2rem 1rem;
  border-bottom: 2px solid rgba(255, 185, 0, 0.2);
  position: relative;
}

.history-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 10%;
  right: 10%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffb900, transparent);
}

.history-title {
  font-size: 1.3rem;
  margin: 0;
  color: #c99725;
  font-weight: 700;
  letter-spacing: 1px;
}

.create-conversation-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1.4rem;
  line-height: 1;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.create-conversation-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.create-conversation-btn:hover::before {
  left: 100%;
}

.create-conversation-btn:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 6px 25px rgba(255, 185, 0, 0.4);
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
}

.conversation-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 1rem;
  margin-bottom: 0.6rem;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 185, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.conversation-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(180deg, #ffb900, #ff6b6b);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.conversation-item:hover {
  background: rgba(255, 185, 0, 0.1);
  transform: translateX(4px) translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.2);
  border-color: rgba(255, 185, 0, 0.3);
}

.conversation-item:hover::before {
  transform: scaleY(1);
}

.conversation-item.active {
  background: linear-gradient(90deg, rgba(255, 185, 0, 0.15) 0%, rgba(255, 107, 107, 0.08) 100%);
  border-color: rgba(255, 185, 0, 0.4);
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.2);
}

.conversation-item.active::before {
  transform: scaleY(1);
}

.conversation-item.unread {
  background: rgba(255, 193, 7, 0.1);
  border-color: rgba(255, 193, 7, 0.3);
}

.conversation-content {
  flex: 1;
  overflow: hidden;
}

.conversation-title {
  font-weight: 600;
  margin-bottom: 0.3rem;
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-preview {
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  margin-bottom: 0.3rem;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-time {
  font-size: 0.75rem;
  color: var(--color-text-secondary);
  text-align: right;
}

.conversation-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
}

.unread-badge {
  background: linear-gradient(135deg, #ff6b6b 0%, #c44569 100%);
  color: white;
  border-radius: 12px;
  padding: 0.15rem 0.5rem;
  font-size: 0.7rem;
  font-weight: 700;
  min-width: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
  animation: badgePulse 2s infinite;
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.delete-conversation-btn {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(196, 69, 105, 0.1) 100%);
  border: 1px solid rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  cursor: pointer;
  font-size: 1.3rem;
  font-weight: bold;
  line-height: 1;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  transform: scale(0.8);
}

.conversation-item:hover .delete-conversation-btn {
  opacity: 1;
  transform: scale(1);
}

.delete-conversation-btn:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #c44569 100%);
  color: white;
  transform: scale(1.15) rotate(90deg);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.5);
  border-color: transparent;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #888;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  opacity: 0.6;
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-state p {
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.create-first-conversation {
  margin-top: 1.5rem;
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #ffb900 0%, #ff6b6b 100%);
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.create-first-conversation::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.create-first-conversation:hover::before {
  left: 100%;
}

.create-first-conversation:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 25px rgba(255, 185, 0, 0.4);
}

.history-footer {
  padding: 1.2rem;
  border-top: 2px solid rgba(255, 185, 0, 0.1);
  text-align: center;
  position: relative;
}

.history-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 15%;
  right: 15%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffb900, #ff6b6b, transparent);
}

.clear-all-btn {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(196, 69, 105, 0.1) 100%);
  border: 1px solid rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  padding: 0.6rem 1.2rem;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.clear-all-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.clear-all-btn:hover::before {
  left: 100%;
}

.clear-all-btn:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #c44569 100%);
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
  border-color: transparent;
}

/* 滚动条样式 */
.history-list::-webkit-scrollbar {
  width: 8px;
}

.history-list::-webkit-scrollbar-track {
  background: rgba(255, 185, 0, 0.05);
  border-radius: 4px;
  margin: 8px 0;
}

.history-list::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #ffb900, #ff6b6b);
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: padding-box;
  transition: all 0.3s ease;
}

.history-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #ff8c00, #c44569);
  background-clip: padding-box;
}

.history-list::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .history-panel {
    height: 100vh;
  }
}
</style>
