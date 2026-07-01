<template>
  <div class="chat-page">
    <aside class="conversation-panel">
      <div class="panel-head">
        <h1>私信</h1>
        <button @click="refresh">刷新</button>
      </div>

      <div class="user-search">
        <input v-model="userKeyword" type="search" placeholder="搜索用户昵称" @keyup.enter="searchUsers" />
        <button @click="searchUsers">搜索</button>
      </div>

      <div v-if="searchedUsers.length" class="search-results">
        <button v-for="user in searchedUsers" :key="user.id" @click="selectUser(user)">
          <img :src="resolveAvatar(user, user.userName)" :alt="user.userName" />
          <div>
            <strong>{{ user.userName || `用户${user.id}` }}</strong>
            <span>点击发起会话</span>
          </div>
        </button>
      </div>

      <div class="conversation-list">
        <button
          v-for="conversation in conversations"
          :key="conversation.otherUserId"
          :class="{ active: activeUserId === conversation.otherUserId }"
          @click="openConversation(conversation)"
        >
          <img :src="resolveAvatar(conversation.otherUser, conversation.otherUser?.userName)" alt="头像" />
          <div>
            <strong>{{ conversation.otherUser?.userName || `用户${conversation.otherUserId}` }}</strong>
            <span>{{ conversation.lastMessage?.content || '暂无消息' }}</span>
          </div>
          <em v-if="conversation.unreadCount">{{ conversation.unreadCount }}</em>
        </button>
        <p v-if="!conversations.length" class="empty">暂无会话，可从论坛作者处发起私信。</p>
      </div>
    </aside>

    <main class="chat-main">
      <header class="chat-header" v-if="activeUserId">
        <button class="back-btn" @click="router.push('/posts')">返回社区</button>
        <img :src="resolveAvatar(activeUser, activeUser?.userName)" alt="头像" />
        <div>
          <h2>{{ activeUser?.userName || `用户${activeUserId}` }}</h2>
          <p>与对方的私信会话已同步到后端数据库</p>
        </div>
      </header>

      <section v-if="activeUserId" ref="messageList" class="message-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ mine: message.senderId === auth.user?.id }"
        >
          <div class="bubble">
            <p>{{ message.content }}</p>
            <span>{{ formatTime(message.createTime) }}</span>
          </div>
        </div>
        <p v-if="!messages.length" class="empty">还没有消息，发送第一条私信吧。</p>
      </section>

      <section v-else class="chat-empty">
        <h2>选择一个会话</h2>
        <p>可以从左侧会话列表、用户搜索，或从论坛帖子作者处发起私信。</p>
      </section>

      <footer v-if="activeUserId" class="chat-input">
        <input v-model="inputMessage" type="text" placeholder="输入消息..." @keyup.enter="sendMessage" />
        <button :disabled="!inputMessage.trim() || sending" @click="sendMessage">
          {{ sending ? '发送中...' : '发送' }}
        </button>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getMessageThread, listConversations, readConversation, sendUserMessage } from '@/api/userMessage'
import { getUserVOById, listUserVOByPage } from '@/api/user'
import { resolveAvatar } from '@/utils/avatar'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const conversations = ref([])
const messages = ref([])
const activeUserId = ref(null)
const activeUser = ref(null)
const inputMessage = ref('')
const sending = ref(false)
const userKeyword = ref('')
const searchedUsers = ref([])
const messageList = ref(null)

function formatTime(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function refresh() {
  const res = await listConversations()
  conversations.value = res.data || []
}

async function loadUser(userId) {
  try {
    activeUser.value = (await getUserVOById(userId)).data
  } catch {
    activeUser.value = { id: userId, userName: `用户${userId}` }
  }
}

async function openConversation(conversation) {
  activeUserId.value = conversation.otherUserId
  activeUser.value = conversation.otherUser || { id: conversation.otherUserId, userName: `用户${conversation.otherUserId}` }
  await loadThread()
}

async function selectUser(user) {
  activeUserId.value = user.id
  activeUser.value = user
  searchedUsers.value = []
  await loadThread()
}

async function loadThread() {
  if (!activeUserId.value) return
  const res = await getMessageThread(activeUserId.value)
  messages.value = res.data || []
  await readConversation(activeUserId.value)
  await refresh()
  await nextTick()
  if (messageList.value) {
    messageList.value.scrollTop = messageList.value.scrollHeight
  }
}

async function sendMessage() {
  if (!activeUserId.value || !inputMessage.value.trim()) return
  sending.value = true
  try {
    await sendUserMessage({ receiverId: activeUserId.value, content: inputMessage.value.trim() })
    inputMessage.value = ''
    await loadThread()
  } finally {
    sending.value = false
  }
}

async function searchUsers() {
  const res = await listUserVOByPage({ current: 1, pageSize: 10, userName: userKeyword.value.trim() })
  searchedUsers.value = (res.data?.records || []).filter((user) => user.id !== auth.user?.id)
}

onMounted(async () => {
  await refresh()
  const queryUserId = Number(route.query.userId)
  if (queryUserId) {
    activeUserId.value = queryUserId
    await loadUser(queryUserId)
    await loadThread()
  }
})
</script>

<style scoped>
.chat-page {
  min-height: calc(100vh - 60px);
  display: grid;
  grid-template-columns: 340px 1fr;
  background: var(--color-background);
}

.conversation-panel {
  border-right: 1px solid var(--color-border);
  background: #fff;
  padding: 18px;
  overflow-y: auto;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.panel-head h1 {
  color: var(--color-primary-dark);
  font-size: 1.55rem;
  font-weight: 800;
}

.panel-head button,
.user-search button,
.chat-input button,
.back-btn {
  padding: 8px 12px;
  border: 0;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
  cursor: pointer;
}

.user-search {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}

.user-search input {
  flex: 1;
  min-width: 0;
}

.search-results,
.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-results {
  margin-bottom: 14px;
}

.search-results button,
.conversation-list button {
  display: grid;
  grid-template-columns: 44px 1fr auto;
  gap: 10px;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  text-align: left;
  cursor: pointer;
}

.conversation-list button.active,
.conversation-list button:hover,
.search-results button:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
}

.search-results img,
.conversation-list img,
.chat-header img {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
}

.conversation-list strong,
.search-results strong {
  display: block;
  color: var(--color-heading);
}

.conversation-list span,
.search-results span {
  display: block;
  max-width: 210px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-text-secondary);
  font-size: 0.84rem;
}

.conversation-list em {
  min-width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-danger);
  color: #fff;
  font-style: normal;
  font-size: 0.75rem;
}

.chat-main {
  min-width: 0;
  display: grid;
  grid-template-rows: auto 1fr auto;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
  background: #fff;
}

.chat-header h2 {
  color: var(--color-heading);
  font-size: 1.1rem;
}

.chat-header p {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.message-list {
  overflow-y: auto;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(201, 168, 76, 0.06), rgba(255, 255, 255, 0)),
    var(--color-background);
}

.message-item {
  display: flex;
  margin-bottom: 12px;
}

.message-item.mine {
  justify-content: flex-end;
}

.bubble {
  max-width: min(560px, 78%);
  padding: 12px 14px;
  border-radius: var(--radius-lg);
  background: #fff;
  border: 1px solid var(--color-border);
}

.message-item.mine .bubble {
  background: var(--color-primary);
  color: #17130e;
  border-color: var(--color-primary);
}

.bubble p {
  line-height: 1.65;
  word-break: break-word;
}

.bubble span {
  display: block;
  margin-top: 6px;
  opacity: 0.7;
  font-size: 0.75rem;
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 14px 18px;
  border-top: 1px solid var(--color-border);
  background: #fff;
}

.chat-input input {
  flex: 1;
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chat-empty,
.empty {
  color: var(--color-text-secondary);
  text-align: center;
}

.chat-empty {
  align-self: center;
}

.chat-empty h2 {
  color: var(--color-heading);
}

@media (max-width: 820px) {
  .chat-page {
    grid-template-columns: 1fr;
  }

  .conversation-panel {
    border-right: 0;
    border-bottom: 1px solid var(--color-border);
    max-height: 360px;
  }
}
</style>
