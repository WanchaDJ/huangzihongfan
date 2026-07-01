<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/posts'
import { newsItems, officialLinks, openExternal } from '@/data/huangzihongfan'
import { uploadFile } from '@/api/file'
import { sendUserMessage } from '@/api/userMessage'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const postStore = usePostStore()

const categories = ['全部', '最新', '音乐作品', '演出活动', '站内交流']
const topicSuggestions = ['巡演现场', '歌曲安利', '会员开箱', '杂志图集', '票务互助', '舞台截图']

const activeCategory = ref('全部')
const composerCategory = ref('站内交流')
const searchText = ref('')
const newPostTitle = ref('')
const newPostContent = ref('')
const newPostTopics = ref('')
const statusMessage = ref('')
const expandedComments = ref([])
const commentInputs = ref({})
const uploadedImages = ref([])
const uploadingImage = ref(false)
const fileInput = ref(null)
const dmPost = ref(null)
const dmContent = ref('')

const activePostId = computed(() => Number(route.query.post || 0))

const communityMetrics = computed(() => [
  { label: '帖子', value: postStore.posts.length },
  { label: '收藏', value: postStore.posts.reduce((sum, post) => sum + Number(post.favourNum || 0), 0) },
  { label: '点赞', value: postStore.posts.reduce((sum, post) => sum + Number(post.likes || 0), 0) }
])

function parseTopics(raw) {
  return Array.from(
    new Set(
      String(raw || '')
        .split(/[#\s,，]+/)
        .map((item) => item.trim())
        .filter(Boolean)
    )
  ).slice(0, 5)
}

const composerTopics = computed(() => parseTopics(newPostTopics.value))

const featuredTopics = computed(() => {
  const counter = new Map()
  postStore.posts.forEach((post) => {
    ;(post.tags || []).forEach((tag) => {
      if (!tag) return
      counter.set(tag, (counter.get(tag) || 0) + 1)
    })
  })
  return [...counter.entries()]
    .sort((a, b) => b[1] - a[1])
    .slice(0, 8)
    .map(([name, count]) => ({ name, count }))
})

const announcements = computed(() => newsItems.slice(0, 4))
const hotPosts = computed(() =>
  [...postStore.posts].sort((a, b) => {
    if ((b.likes || 0) !== (a.likes || 0)) return (b.likes || 0) - (a.likes || 0)
    return new Date(b.createTime || 0) - new Date(a.createTime || 0)
  }).slice(0, 5)
)

const posts = computed(() => {
  const keyword = searchText.value.trim().toLowerCase()
  let list = [...postStore.posts]

  if (activeCategory.value !== '全部' && activeCategory.value !== '最新') {
    list = list.filter((post) => post.tags?.includes(activeCategory.value))
  }

  list.sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))

  if (keyword) {
    list = list.filter((post) =>
      `${post.title}${post.content}${post.username}${(post.tags || []).join('')}`.toLowerCase().includes(keyword)
    )
  }

  if (activePostId.value) {
    list.sort((a, b) => {
      if (a.id === activePostId.value) return -1
      if (b.id === activePostId.value) return 1
      return new Date(b.createTime || 0) - new Date(a.createTime || 0)
    })
  }

  return list
})

function requireLogin() {
  if (!auth.isAuthenticated) {
    statusMessage.value = '请先登录后再使用社区互动功能。'
    router.push({ path: '/login', query: { redirect: '/posts' } })
    return false
  }
  return true
}

function appendTopic(topic) {
  const topics = parseTopics(`${newPostTopics.value} ${topic}`)
  newPostTopics.value = topics.join(' ')
}

function applyTag(tag) {
  if (categories.includes(tag)) {
    activeCategory.value = tag
    return
  }
  searchText.value = tag
}

function removeUploadedImage(url) {
  uploadedImages.value = uploadedImages.value.filter((image) => image.url !== url)
}

function chooseImages() {
  if (!requireLogin()) return
  fileInput.value?.click()
}

async function handleImages(event) {
  const remain = Math.max(0, 6 - uploadedImages.value.length)
  const files = Array.from(event.target.files || []).slice(0, remain)
  if (!files.length) return

  uploadingImage.value = true
  statusMessage.value = ''
  try {
    const results = []
    for (const file of files) {
      const res = await uploadFile(file, 'community_image')
      results.push({ name: file.name, url: res.data })
    }
    uploadedImages.value = [...uploadedImages.value, ...results]
    statusMessage.value = '图片已上传，将随帖子一起写入社区内容。'
  } catch (error) {
    statusMessage.value = error.message || '图片上传失败。'
  } finally {
    uploadingImage.value = false
    event.target.value = ''
  }
}

async function submitPost() {
  if (!requireLogin()) return
  if (!newPostContent.value.trim() && !uploadedImages.value.length) return

  statusMessage.value = ''
  const imageMarkdown = uploadedImages.value
    .map((image, index) => `![图片${index + 1}](${image.url})`)
    .join('\n')
  const tags = Array.from(new Set([composerCategory.value, ...composerTopics.value])).filter(Boolean)

  try {
    await postStore.addPost({
      title: newPostTitle.value.trim() || '分享',
      content: [newPostContent.value.trim(), imageMarkdown].filter(Boolean).join('\n\n'),
      tags
    })
    newPostTitle.value = ''
    newPostContent.value = ''
    newPostTopics.value = ''
    uploadedImages.value = []
    composerCategory.value = '站内交流'
    statusMessage.value = '帖子已发布。'
  } catch (error) {
    statusMessage.value = error.message || '发布失败，请确认后端服务可用。'
  }
}

async function refreshPosts() {
  await postStore.fetchPosts()
}

async function toggleLike(postId) {
  if (!requireLogin()) return
  await postStore.toggleLike(postId)
}

async function toggleFavorite(postId) {
  if (!requireLogin()) return
  await postStore.toggleFavorite(postId)
}

async function toggleComments(postId) {
  const index = expandedComments.value.indexOf(postId)
  if (index >= 0) {
    expandedComments.value.splice(index, 1)
    return
  }
  expandedComments.value.push(postId)
  await postStore.fetchPostComments(postId)
}

async function addComment(postId) {
  if (!requireLogin()) return
  const content = commentInputs.value[postId]?.trim()
  if (!content) return
  await postStore.addComment(postId, content)
  commentInputs.value[postId] = ''
}

async function deletePost(postId) {
  if (!requireLogin()) return
  if (!window.confirm('确定删除这条帖子吗？')) return
  try {
    await postStore.deletePost(postId)
    statusMessage.value = '帖子已删除。'
  } catch {
    statusMessage.value = '删除失败，只能删除自己发布的帖子。'
  }
}

async function sharePost(post) {
  const url = `${window.location.origin}/posts?post=${post.id}`
  try {
    await navigator.clipboard.writeText(url)
    statusMessage.value = '帖子链接已复制。'
  } catch {
    statusMessage.value = url
  }
}

function openMessage(post) {
  if (!requireLogin()) return
  if (post.userId === auth.user?.id) {
    statusMessage.value = '不能给自己发送私信。'
    return
  }
  dmPost.value = post
  dmContent.value = `你好，我看到你在社区发布的《${post.title || '分享'}》，想和你聊聊。`
}

async function sendMessage() {
  if (!dmPost.value || !dmContent.value.trim()) return
  try {
    await sendUserMessage({ receiverId: dmPost.value.userId, content: dmContent.value.trim() })
    const otherUserId = dmPost.value.userId
    dmPost.value = null
    dmContent.value = ''
    router.push({ path: '/chat', query: { userId: otherUserId } })
  } catch (error) {
    statusMessage.value = error.message || '私信发送失败。'
  }
}

onMounted(() => {
  postStore.fetchPosts()
})
</script>

<template>
  <div class="post-page">
    <section class="page-hero">
      <span class="eyebrow">COMMUNITY</span>
      <h1>粉丝社区</h1>
      <p>支持图文帖子、话题标签、点赞、收藏、评论与私信，帖子内容直接写入后端并保存在数据库。</p>
      <div class="hero-metrics">
        <div v-for="item in communityMetrics" :key="item.label" class="metric-item">
          <strong>{{ item.value }}</strong>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </section>

    <main class="container community-layout">
      <aside class="left-column">
        <section class="sidebar-card">
          <h2>内容分区</h2>
          <button
            v-for="category in categories"
            :key="category"
            :class="{ active: activeCategory === category }"
            @click="activeCategory = category"
          >
            {{ category }}
          </button>
        </section>

        <section class="sidebar-card">
          <h2>热门话题</h2>
          <button v-for="topic in featuredTopics" :key="topic.name" @click="applyTag(topic.name)">
            <span class="side-title">#{{ topic.name }}</span>
            <em>{{ topic.count }} 条内容</em>
          </button>
        </section>

        <section class="sidebar-card">
          <h2>站外入口</h2>
          <button @click="openExternal(officialLinks.weibo)">微博主页</button>
          <button @click="openExternal(officialLinks.xiaohongshu)">小红书主页</button>
          <button @click="openExternal(officialLinks.douyin)">抖音主页</button>
          <button @click="openExternal(officialLinks.appleMusic)">Apple Music</button>
        </section>
      </aside>

      <section class="feed-column">
        <div class="composer">
          <div class="composer-head">
            <div>
              <span class="composer-mark">发布帖子</span>
              <h2>像微博一样发图文与话题</h2>
            </div>
            <button class="btn-outline" @click="refreshPosts">刷新内容</button>
          </div>

          <input v-model="newPostTitle" type="text" maxlength="80" placeholder="标题，可选" />
          <textarea
            v-model="newPostContent"
            rows="5"
            maxlength="4000"
            placeholder="写下你的内容，可以只发图片，也可以图文一起发。"
          ></textarea>

          <div class="composer-grid">
            <div class="field-block">
              <label>分区</label>
              <div class="chip-row">
                <button
                  v-for="category in categories.filter((item) => item !== '全部' && item !== '最新')"
                  :key="category"
                  :class="{ active: composerCategory === category }"
                  @click="composerCategory = category"
                >
                  {{ category }}
                </button>
              </div>
            </div>

            <div class="field-block">
              <label>话题</label>
              <input v-model="newPostTopics" type="text" placeholder="输入多个话题，用空格或逗号分隔" />
              <div class="chip-row compact">
                <button v-for="topic in topicSuggestions" :key="topic" @click="appendTopic(topic)">#{{ topic }}</button>
              </div>
              <div v-if="composerTopics.length" class="topic-preview">
                <span v-for="topic in composerTopics" :key="topic">#{{ topic }}</span>
              </div>
            </div>
          </div>

          <div v-if="uploadedImages.length" class="preview-grid">
            <div v-for="image in uploadedImages" :key="image.url" class="preview-item">
              <img :src="image.url" :alt="image.name" />
              <button type="button" @click="removeUploadedImage(image.url)">移除</button>
            </div>
          </div>

          <div class="composer-actions">
            <input ref="fileInput" type="file" accept="image/*" multiple hidden @change="handleImages" />
            <button class="btn-outline" :disabled="uploadingImage || uploadedImages.length >= 6" @click="chooseImages">
              {{ uploadingImage ? '上传中...' : `上传图片 ${uploadedImages.length}/6` }}
            </button>
            <button class="btn-primary" :disabled="!newPostContent.trim() && !uploadedImages.length" @click="submitPost">
              发布帖子
            </button>
          </div>
        </div>

        <p v-if="statusMessage" class="status-message">{{ statusMessage }}</p>

        <div class="tools">
          <input v-model="searchText" type="search" placeholder="搜索标题、作者、正文或话题" />
          <button class="btn-outline" @click="refreshPosts">刷新</button>
        </div>

        <section class="post-list">
          <article
            v-for="post in posts"
            :key="post.id"
            class="post-card"
            :class="{ highlighted: post.id === activePostId }"
          >
            <div class="post-header">
              <img :src="post.avatar" :alt="post.username" />
              <div class="post-title-wrap">
                <h2>{{ post.title || '分享' }}</h2>
                <p>{{ post.username }} · {{ post.time }}</p>
              </div>
              <button
                v-if="auth.user?.id === post.userId || auth.user?.userRole === 'admin'"
                class="danger-link"
                @click="deletePost(post.id)"
              >
                删除
              </button>
            </div>

            <p class="post-content">{{ post.content }}</p>

            <div v-if="post.images?.length" class="post-images">
              <img v-for="image in post.images" :key="image" :src="image" alt="帖子图片" />
            </div>

            <div v-if="post.tags?.length" class="post-tags">
              <button v-for="tag in post.tags" :key="tag" @click="applyTag(tag)">#{{ tag }}</button>
            </div>

            <div class="post-actions">
              <button :class="{ active: post.liked }" @click="toggleLike(post.id)">点赞 {{ post.likes || 0 }}</button>
              <button :class="{ active: post.favorited }" @click="toggleFavorite(post.id)">收藏 {{ post.favourNum || 0 }}</button>
              <button @click="toggleComments(post.id)">评论 {{ post.comments?.length || 0 }}</button>
              <button @click="openMessage(post)">私信</button>
              <button @click="sharePost(post)">分享</button>
            </div>

            <div v-if="expandedComments.includes(post.id)" class="comments">
              <div v-if="post.comments?.length" class="comment-list">
                <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                  <img :src="comment.avatar" :alt="comment.username" />
                  <div>
                    <strong>{{ comment.username }}</strong>
                    <span>{{ comment.time }}</span>
                    <p>{{ comment.content }}</p>
                  </div>
                </div>
              </div>
              <p v-else class="empty">暂无评论。</p>
              <div class="comment-form">
                <input v-model="commentInputs[post.id]" type="text" placeholder="写评论" @keyup.enter="addComment(post.id)" />
                <button @click="addComment(post.id)">发送</button>
              </div>
            </div>
          </article>

          <p v-if="posts.length === 0" class="empty">暂无帖子。</p>
        </section>
      </section>

      <aside class="right-column">
        <section class="sidebar-card">
          <h2>公开动态</h2>
          <button v-for="item in announcements" :key="item.id" @click="openExternal(item.sourceUrl)">
            <span class="side-title">{{ item.title }}</span>
            <em>{{ item.date }}</em>
          </button>
        </section>

        <section class="sidebar-card">
          <h2>热门帖子</h2>
          <button v-for="item in hotPosts" :key="item.id" @click="router.push({ path: '/posts', query: { post: item.id } })">
            <span class="side-title">{{ item.title || '分享' }}</span>
            <em>点赞 {{ item.likes || 0 }}</em>
          </button>
        </section>

        <section class="sidebar-card">
          <h2>私信提醒</h2>
          <p>可直接从帖子发起私信，也可以进入会话页查看全部聊天记录与未读消息。</p>
          <button class="wide-btn" @click="router.push('/chat')">进入私信页</button>
        </section>
      </aside>
    </main>

    <div v-if="dmPost" class="modal" @click="dmPost = null">
      <div class="modal-box" @click.stop>
        <button class="modal-close" @click="dmPost = null">×</button>
        <h2>发送私信给 {{ dmPost.username }}</h2>
        <textarea v-model="dmContent" rows="5"></textarea>
        <button class="btn-primary" @click="sendMessage">发送并进入私信</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-page {
  min-height: 100vh;
  background: var(--color-background);
}

.page-hero {
  padding: 56px 24px;
  text-align: center;
  color: #fff;
  background: linear-gradient(135deg, #171411 0%, #2a2117 48%, #191726 100%);
}

.eyebrow,
.composer-mark {
  color: var(--color-primary);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.page-hero h1 {
  margin: 8px 0 10px;
  color: #f4cf6a;
  font-size: 2.4rem;
}

.page-hero p {
  margin: 0 auto;
  max-width: 760px;
  color: rgba(255, 255, 255, 0.78);
}

.hero-metrics {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 22px;
  flex-wrap: wrap;
}

.metric-item {
  min-width: 112px;
  padding: 14px 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(8px);
}

.metric-item strong {
  display: block;
  color: #f4cf6a;
  font-size: 1.2rem;
}

.metric-item span {
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.82rem;
}

.community-layout {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr) 260px;
  gap: 22px;
  padding-top: 34px;
  padding-bottom: 60px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card,
.composer,
.post-card {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.sidebar-card {
  padding: 18px;
}

.sidebar-card h2,
.composer h2,
.post-header h2,
.modal-box h2 {
  color: var(--color-heading);
}

.sidebar-card h2 {
  margin-bottom: 12px;
  font-size: 1rem;
}

.sidebar-card button,
.wide-btn {
  width: 100%;
  display: block;
  margin-bottom: 8px;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  text-align: left;
  cursor: pointer;
}

.sidebar-card button:hover,
.sidebar-card button.active,
.wide-btn {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.side-title {
  display: block;
  margin-bottom: 4px;
  font-weight: 700;
}

.sidebar-card em {
  color: var(--color-text-secondary);
  font-size: 0.78rem;
  font-style: normal;
}

.sidebar-card p {
  color: var(--color-text-secondary);
  line-height: 1.7;
  margin-bottom: 12px;
}

.composer {
  padding: 20px;
  margin-bottom: 16px;
}

.composer-head,
.composer-actions,
.tools,
.post-actions,
.comment-form {
  display: flex;
  align-items: center;
  gap: 10px;
}

.composer-head {
  justify-content: space-between;
  margin-bottom: 14px;
}

.composer-head h2 {
  margin-top: 6px;
  font-size: 1.2rem;
}

.composer input,
.composer textarea,
.tools input,
.comment-form input,
.modal-box textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  outline: none;
  font: inherit;
}

.composer textarea,
.modal-box textarea {
  margin-top: 10px;
  resize: vertical;
}

.composer-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 14px;
}

.field-block {
  padding: 14px;
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.field-block label {
  display: block;
  margin-bottom: 10px;
  color: var(--color-heading);
  font-weight: 700;
}

.chip-row,
.topic-preview,
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip-row.compact {
  margin-top: 10px;
}

.chip-row button,
.topic-preview span,
.post-tags button {
  padding: 7px 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: #fff;
  color: var(--color-text-secondary);
  cursor: pointer;
  font-size: 0.82rem;
}

.chip-row button.active,
.chip-row button:hover,
.post-tags button:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.topic-preview {
  margin-top: 10px;
}

.topic-preview span {
  border-color: rgba(212, 170, 54, 0.26);
  background: rgba(212, 170, 54, 0.08);
  color: var(--color-primary-dark);
}

.preview-grid,
.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 14px;
}

.preview-item {
  position: relative;
}

.preview-item img,
.post-images img {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: var(--radius-sm);
  object-fit: cover;
}

.preview-item button {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border: 0;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.68);
  color: #fff;
  cursor: pointer;
}

.composer-actions {
  justify-content: flex-end;
  margin-top: 16px;
}

.status-message,
.tools {
  margin-bottom: 16px;
}

.status-message {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.tools {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.post-card {
  padding: 18px;
}

.post-card.highlighted {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.post-header img,
.comment-item img {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
}

.post-title-wrap {
  min-width: 0;
}

.post-header h2 {
  margin-bottom: 4px;
  font-size: 1.05rem;
}

.post-header p,
.comment-item span {
  color: var(--color-text-secondary);
  font-size: 0.82rem;
}

.danger-link {
  margin-left: auto;
  border: 0;
  background: transparent;
  color: var(--color-danger);
  cursor: pointer;
}

.post-content {
  color: var(--color-text);
  line-height: 1.76;
  white-space: pre-wrap;
}

.post-tags {
  margin-top: 12px;
}

.post-actions {
  flex-wrap: wrap;
  padding-top: 14px;
  margin-top: 14px;
  border-top: 1px solid var(--color-border);
}

.post-actions button,
.comment-form button,
.btn-outline,
.btn-primary {
  padding: 9px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  cursor: pointer;
}

.btn-primary {
  border-color: var(--color-primary);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.post-actions button.active,
.post-actions button:hover,
.comment-form button,
.btn-outline:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.comments {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 12px;
}

.comment-item {
  display: flex;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  background: var(--color-background-soft);
}

.comment-item strong {
  color: var(--color-heading);
  margin-right: 8px;
}

.comment-item p {
  margin-top: 5px;
  color: var(--color-text);
}

.empty {
  padding: 26px 0;
  color: var(--color-text-secondary);
  text-align: center;
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

.modal-box {
  position: relative;
  width: min(520px, 100%);
  padding: 24px;
  border-radius: var(--radius-lg);
  background: #fff;
}

.modal-box .btn-primary {
  width: 100%;
  margin-top: 12px;
}

.modal-close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 50%;
  background: var(--color-background-soft);
  cursor: pointer;
}

@media (max-width: 1120px) {
  .community-layout {
    grid-template-columns: 1fr;
  }

  .left-column,
  .right-column {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .composer-grid,
  .left-column,
  .right-column,
  .preview-grid,
  .post-images {
    grid-template-columns: 1fr;
  }

  .tools,
  .composer-actions,
  .comment-form,
  .composer-head {
    align-items: stretch;
    grid-template-columns: 1fr;
    flex-direction: column;
  }

  .hero-metrics {
    justify-content: stretch;
  }

  .metric-item {
    flex: 1 1 0;
  }
}
</style>
