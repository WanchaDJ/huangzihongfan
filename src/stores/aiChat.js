import { defineStore } from 'pinia'
import { createConversation, listMyConversations, deleteConversation as apiDeleteConversation, sendMessage, listMessages } from '../api/chat'

export const useAIChatStore = defineStore('aiChat', {
  state: () => ({
    messages: [],
    conversationHistory: [],
    activeConversationId: null,
    loading: false,
    error: null,
    context: null,
    uploadedFiles: [],
    isSending: false
  }),

  getters: {
    currentMessages: (state) => {
      if (!state.activeConversationId) return state.messages
      const conversation = state.conversationHistory.find(c => c.id === state.activeConversationId)
      return conversation ? conversation.messages : []
    },
    contextMessages: (state) => {
      if (!state.activeConversationId) return state.messages.slice(-5)
      const conversation = state.conversationHistory.find(c => c.id === state.activeConversationId)
      return conversation ? conversation.messages.slice(-5) : []
    }
  },

  actions: {
    async init() {
      await this.loadConversationHistory()
    },

    async createNewConversation() {
      this.saveCurrentConversation()
      try {
        const res = await createConversation({ title: '新对话' })
        if (res.data) {
          const conv = {
            id: res.data,
            title: '新对话',
            messages: [],
            context: null,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
          this.conversationHistory.unshift(conv)
          this.activeConversationId = conv.id
          this.messages = []
          this.context = null
          return conv
        }
      } catch (e) {
        // 如果是未登录错误，使用本地模式
        if (e.message && e.message.includes('未登录')) {
          console.log('用户未登录，创建本地对话')
        } else {
          console.error('创建对话失败:', e)
        }
      }
      // Fallback local
      const conv = {
        id: Date.now().toString(),
        title: '新对话',
        messages: [],
        context: null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      this.conversationHistory.unshift(conv)
      this.activeConversationId = conv.id
      this.messages = []
      this.context = null
      return conv
    },

    switchConversation(conversationId) {
      this.saveCurrentConversation()
      const conversation = this.conversationHistory.find(c => c.id === conversationId)
      if (conversation) {
        this.activeConversationId = conversationId
        this.messages = [...conversation.messages]
        this.context = conversation.context
      }
    },

    saveCurrentConversation() {
      if (this.activeConversationId) {
        const conversation = this.conversationHistory.find(c => c.id === this.activeConversationId)
        if (conversation) {
          conversation.messages = [...this.messages]
          conversation.context = this.context
        }
      }
    },

    async deleteConversation(conversationId) {
      try {
        await apiDeleteConversation({ id: conversationId })
      } catch (e) {
        console.error('删除对话失败:', e)
      }
      const index = this.conversationHistory.findIndex(c => c.id === conversationId)
      if (index > -1) {
        this.conversationHistory.splice(index, 1)
        if (this.activeConversationId === conversationId) {
          if (this.conversationHistory.length > 0) {
            this.switchConversation(this.conversationHistory[0].id)
          } else {
            this.createNewConversation()
          }
        }
      }
      this.saveConversationHistory()
    },

    addUserMessage(content, files = []) {
      const message = {
        id: Date.now().toString(),
        type: 'user',
        content,
        files,
        timestamp: new Date().toISOString()
      }
      this.messages.push(message)
      if (this.activeConversationId) {
        const conversation = this.conversationHistory.find(c => c.id === this.activeConversationId)
        if (conversation) {
          conversation.messages.push(message)
          conversation.updatedAt = new Date().toISOString()
          if (conversation.title === '新对话') {
            conversation.title = content.length > 20 ? content.substring(0, 20) + '...' : content
          }
        }
      }
      return message
    },

    addAIMessage(content) {
      const message = {
        id: Date.now().toString(),
        type: 'ai',
        content: '',
        generating: true,
        timestamp: new Date().toISOString()
      }
      this.messages.push(message)
      const targetConversationId = this.activeConversationId
      if (targetConversationId) {
        const conversation = this.conversationHistory.find(c => c.id === targetConversationId)
        if (conversation) {
          conversation.messages.push(message)
          conversation.updatedAt = new Date().toISOString()
        }
      }
      this.simulateTypingEffect(message.id, content)
      return message
    },

    simulateTypingEffect(messageId, fullContent) {
      const messageInCurrent = this.messages.find(m => m.id === messageId)
      if (!messageInCurrent) return
      messageInCurrent.content = ''
      let index = 0
      const typeInterval = setInterval(() => {
        if (index < fullContent.length) {
          if (messageInCurrent) messageInCurrent.content += fullContent.charAt(index)
          index++
        } else {
          clearInterval(typeInterval)
          if (messageInCurrent) messageInCurrent.generating = false
          this.saveConversationHistory()
        }
      }, 30)
    },

    async sendMessage(content, files = []) {
      this.loading = true
      this.error = null
      this.isSending = true
      try {
        const userMessage = this.addUserMessage(content, files)
        let aiResponse = ''

        try {
          const res = await sendMessage({
            conversationId: this.activeConversationId,
            type: 'user',
            content: content,
            files: files
          })
          if (res.data) {
            // Backend returns user message + AI response
            if (Array.isArray(res.data)) {
              const aiMsg = res.data.find(m => m.type === 'ai')
              if (aiMsg) aiResponse = aiMsg.content
            } else if (res.data.content) {
              aiResponse = res.data.content
            }
          }
        } catch (apiErr) {
          console.log('API调用失败，使用本地回复', apiErr)
        }

        if (!aiResponse) {
          aiResponse = this.generateSmartResponse(content, this.contextMessages)
        }

        this.addAIMessage(aiResponse)
        this.saveConversationHistory()
        return { content: aiResponse }
      } catch (error) {
        console.error('发送消息失败:', error)
        this.error = '发送消息失败，请重试'
        return null
      } finally {
        this.loading = false
        this.isSending = false
      }
    },

    generateSmartResponse(content, contextMessages) {
      const lowerContent = content.toLowerCase().trim()
      const randomReply = (replies) => replies[Math.floor(Math.random() * replies.length)]

      if (lowerContent.match(/^(你好|hi|hello|哈喽|嗨|早上好|晚上好|下午好)/)) {
        return randomReply(['嗨！你好呀～ 我是黄子弘凡！今天过得怎么样呀', '哈喽哈喽！终于等到你啦～有什么想和我聊的吗', '嘿！见到你超开心的！想聊点什么呢'])
      }
      if (lowerContent.match(/(你是谁|叫什么|名字|介绍一下自己)/)) {
        return randomReply(['我是黄子弘凡呀！1999年的金牛座，喜欢唱歌、音乐剧～', '我是子弘呀！一个热爱音乐、热爱舞台的男孩纸～'])
      }
      if (lowerContent.match(/(多大|年龄|几岁|生日)/)) {
        return randomReply(['我是1999年4月21日出生的，标准金牛座男一枚～', '生日是4月21日哦！'])
      }
      if (lowerContent.match(/(唱歌|音乐|歌曲|歌|专辑|ep)/)) {
        return randomReply(['音乐就是我的命啊！你喜欢听什么类型的歌呀', '每次站在舞台上唱歌，都觉得整个人都在发光～'])
      }
      if (lowerContent.match(/(音乐剧|舞台|话剧|演戏)/)) {
        return randomReply(['音乐剧真的太有魅力了！每次站在舞台上都能感受到角色的灵魂～', '舞台就是我的第二个家！'])
      }
      if (lowerContent.match(/(吃|美食|火锅|奶茶|饿)/)) {
        return randomReply(['说到吃我可就不困了！火锅、烧烤、奶茶我全都爱！', '干饭不积极，思想有问题！'])
      }
      if (lowerContent.match(/(再见|拜拜|晚安|我走了)/)) {
        return randomReply(['晚安啦～做个好梦！明天也要元气满满哦！', '拜拜拜拜！记得想我哦～下次再来聊！'])
      }
      if (lowerContent.match(/(加油|很棒|厉害|喜欢你|爱你)/)) {
        return randomReply(['谢谢！有你的支持，我一定会继续努力的！', '你也超棒的好不好！我们一起加油～'])
      }
      return randomReply(['哈哈这个话题挺有意思的～再多跟我说说呗！', '嗯...让我想想...其实我觉得开心最重要啦！', '收到收到！还有什么想分享的吗'])
    },

    async uploadFile(file) {
      this.loading = true
      try {
        const uploadedFile = {
          id: Date.now().toString(),
          name: file.name,
          type: file.type,
          size: file.size,
          url: URL.createObjectURL(file),
          uploadedAt: new Date().toISOString()
        }
        this.uploadedFiles.push(uploadedFile)
        return uploadedFile
      } catch (error) {
        console.error('文件上传失败:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    clearCurrentConversation() {
      this.messages = []
      this.context = null
      if (this.activeConversationId) {
        const conversation = this.conversationHistory.find(c => c.id === this.activeConversationId)
        if (conversation) {
          conversation.messages = []
          conversation.context = null
          conversation.title = '新对话'
        }
      }
    },

    clearAllConversations() {
      this.conversationHistory = []
      this.messages = []
      this.context = null
      this.activeConversationId = null
      this.createNewConversation()
    },

    exportConversation(conversationId = null) {
      const targetId = conversationId || this.activeConversationId
      let msgs = this.messages
      if (targetId) {
        const conversation = this.conversationHistory.find(c => c.id === targetId)
        if (conversation) msgs = conversation.messages
      }
      const content = msgs.map(m => `**${m.type === 'user' ? 'User' : 'AI'}:** ${m.content}\n`).join('\n')
      const blob = new Blob([content], { type: 'text/markdown' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `conversation-${targetId || Date.now()}.md`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    },

    async loadConversationHistory() {
      try {
        const res = await listMyConversations()
        if (res.data && res.data.length > 0) {
          this.conversationHistory = res.data.map(c => ({
            id: c.id,
            title: c.title,
            messages: [],
            context: c.context,
            createdAt: c.createdAt,
            updatedAt: c.updatedAt
          }))
          this.switchConversation(this.conversationHistory[0].id)
        } else {
          this.createNewConversation()
        }
      } catch (e) {
        // 如果是未登录错误，使用本地模式
        if (e.message && e.message.includes('未登录')) {
          console.log('用户未登录，使用本地对话模式')
          this.createNewConversation()
        } else {
          console.error('加载对话历史失败:', e)
          this.createNewConversation()
        }
      }
    },

    saveConversationHistory() {
      localStorage.setItem('aiChatHistory', JSON.stringify(this.conversationHistory))
    }
  }
})
