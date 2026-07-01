<template>
  <div class="profile-section">
    <div class="section-header">
      <h2>我发表的帖子</h2>
      <div class="section-actions">
        <button class="btn btn-primary" @click="createNewPost">
          <span class="btn-icon">✍️</span>
          发表新帖子
        </button>
      </div>
    </div>

    <!-- 帖子统计信息 -->
    <div class="user-stats">
      <div class="stat-item">
        <div class="stat-number">{{ totalPosts }}</div>
        <div class="stat-label">总帖子数</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ totalLikes }}</div>
        <div class="stat-label">总获赞数</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ totalComments }}</div>
        <div class="stat-label">总评论数</div>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div v-if="userPosts.length > 0" class="posts-list">
      <div v-for="post in userPosts" :key="post.id" class="user-post-item">
        <div class="post-header">
          <h3 class="post-title">{{ post.title }}</h3>
          <div class="post-actions">
            <button class="action-btn edit-btn" @click="editPost(post.id)">
              <span class="btn-icon">✏️</span>
              编辑
            </button>
            <button class="action-btn delete-btn" @click="deletePost(post.id)">
              <span class="btn-icon">🗑️</span>
              删除
            </button>
          </div>
        </div>
        <div class="post-meta">
          <span class="post-category">
            <span class="category-icon">🏷️</span>
            {{ post.category || '未分类' }}
          </span>
          <span class="post-time">{{ formatPostTime(post.time) }}</span>
        </div>
        <div class="post-preview">
          {{ post.content.substring(0, 200) }}
          {{ post.content.length > 200 ? '...' : '' }}
        </div>
        <div class="post-engagement">
          <div class="engagement-item">
            <span class="engagement-icon">❤️</span>
            <span>{{ post.likes || 0 }} 赞</span>
          </div>
          <div class="engagement-item">
            <span class="engagement-icon">💬</span>
            <span>{{ post.comments?.length || 0 }} 评论</span>
          </div>
          <div class="engagement-item">
            <span class="engagement-icon">👁️</span>
            <span>{{ post.views || 0 }} 浏览</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-message">
      <div class="empty-icon">📝</div>
      <div class="empty-text">你还没有发表任何帖子</div>
      <div class="empty-subtext">分享你的想法和见解，与大家交流吧</div>
      <button class="btn btn-primary mt-4" @click="createNewPost">
        <span class="btn-icon">✍️</span>
        发表第一篇帖子
      </button>
    </div>

    <!-- 加载更多 -->
    <div v-if="userPosts.length > 0 && hasMorePosts" class="load-more-container">
      <button class="btn btn-outline" @click="loadMorePosts">
        <span class="btn-icon">🔄</span>
        加载更多
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostStore } from '@/stores/posts'

const router = useRouter()
const authStore = useAuthStore()
const postStore = usePostStore()

// 响应式数据
const userPosts = ref([])
const hasMorePosts = ref(true)
const currentPage = ref(1)

// 计算属性
const totalPosts = computed(() => userPosts.value.length)
const totalLikes = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + (post.likes || 0), 0)
})
const totalComments = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + (post.comments?.length || 0), 0)
})

// 模拟加载用户帖子
const loadUserPosts = () => {
  // 从store获取数据
  const posts = postStore.posts.filter(post => post.authorId === authStore.user?.id)
  
  // 模拟分页数据
  const pageSize = 10
  const startIndex = (currentPage.value - 1) * pageSize
  const endIndex = startIndex + pageSize
  
  const newPosts = posts.slice(startIndex, endIndex).map(post => ({
    ...post,
    category: post.category || '生活',
    views: Math.floor(Math.random() * 500) + 10
  }))
  
  if (currentPage.value === 1) {
    userPosts.value = newPosts
  } else {
    userPosts.value = [...userPosts.value, ...newPosts]
  }
  
  // 检查是否还有更多帖子
  hasMorePosts.value = endIndex < posts.length
}

// 加载更多帖子
const loadMorePosts = () => {
  if (hasMorePosts.value) {
    currentPage.value++
    loadUserPosts()
  }
}

// 创建新帖子
const createNewPost = () => {
  // 这里可以跳转到帖子创建页面
  alert('创建新帖子功能开发中...')
}

// 编辑帖子
const editPost = (postId) => {
  alert(`编辑帖子 ID: ${postId}`)
}

// 删除帖子
const deletePost = (postId) => {
  if (confirm('确定要删除这篇帖子吗？')) {
    const index = userPosts.value.findIndex(post => post.id === postId)
    if (index !== -1) {
      userPosts.value.splice(index, 1)
    }
  }
}

// 格式化帖子时间
const formatPostTime = (time) => {
  if (!time) return ''
  
  const date = new Date(time)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  if (diffMins < 1) {
    return '刚刚'
  } else if (diffMins < 60) {
    return `${diffMins}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadUserPosts()
})
</script>

<style scoped>
/* 帖子列表样式 */
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-post-item {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid #e0e0e0;
}

.user-post-item:hover {
  background-color: #f5f5f5;
  border-color: #ffb900;
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.1);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  flex-wrap: wrap;
  gap: 10px;
}

.post-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.edit-btn {
  background-color: #e3f2fd;
  color: #1976d2;
}

.edit-btn:hover {
  background-color: #bbdefb;
  color: #1565c0;
}

.delete-btn {
  background-color: #ffebee;
  color: #c62828;
}

.delete-btn:hover {
  background-color: #ffcdd2;
  color: #b71c1c;
}

.btn-icon {
  font-size: 0.9rem;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 0.85rem;
  color: #666;
  flex-wrap: wrap;
  gap: 10px;
}

.post-category {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
}

.category-icon {
  font-size: 0.75rem;
}

.post-time {
  font-size: 0.8rem;
  color: #999;
}

.post-preview {
  margin-bottom: 15px;
  color: #333;
  line-height: 1.5;
  font-size: 0.95rem;
}

.post-engagement {
  display: flex;
  gap: 20px;
  font-size: 0.9rem;
  color: #666;
  padding-top: 10px;
  border-top: 1px solid #e0e0e0;
}

.engagement-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.engagement-icon {
  font-size: 1rem;
}

/* 空状态样式 */
.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  color: #e0e0e0;
}

.empty-text {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 10px;
  color: #666;
}

.empty-subtext {
  font-size: 0.95rem;
  margin-bottom: 20px;
  color: #999;
}

/* 加载更多样式 */
.load-more-container {
  text-align: center;
  padding: 20px 0;
}

/* 按钮通用样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background-color: #ffb900;
  color: #fff;
}

.btn-primary:hover {
  background-color: #ff9800;
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(255, 185, 0, 0.3);
}

.btn-outline {
  background-color: #fff;
  color: #ffb900;
  border: 1px solid #ffb900;
}

.btn-outline:hover {
  background-color: #ffb900;
  color: #fff;
  transform: translateY(-1px);
}

/* 统计信息样式 */
.user-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 0.85rem;
  color: #666;
  white-space: nowrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-stats {
    gap: 20px;
  }
  
  .stat-item {
    min-width: 80px;
  }
  
  .stat-number {
    font-size: 1.2rem;
  }
  
  .post-header {
    flex-direction: column;
  }
  
  .post-engagement {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>