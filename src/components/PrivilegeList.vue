<template>
  <div class="privilege-list">
    <h2 class="section-title">会员权益</h2>
    
    <div class="privilege-tabs">
      <button 
        v-for="tab in privilegeTabs" 
        :key="tab.value"
        class="tab-btn"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>
    
    <div class="privileges-container">
      <div 
        ref="scrollContainer"
        class="virtual-list"
        :style="{ height: `${containerHeight}px` }"
      >
        <div 
          class="virtual-list-content"
          :style="{ height: `${virtualList?.totalHeight || 0}px` }"
        >
          <div v-if="virtualList?.visibleItems">
            <div
              v-for="virtualItem in virtualList.visibleItems"
              :key="virtualItem.data.id"
              class="privilege-item"
              :style="{
                transform: `translateY(${virtualItem.start}px)`,
                height: `${virtualItem.size}px`
              }"
              @click="showPrivilegeDetail(virtualItem.data)"
            >
              <div class="privilege-icon">{{ virtualItem.data.icon }}</div>
              <div class="privilege-info">
                <div class="privilege-name">{{ virtualItem.data.name }}</div>
                <div class="privilege-description">{{ virtualItem.data.description }}</div>
              </div>
              <div class="privilege-action"><span class="icon">→</span></div>
            </div>
          </div>
          <div v-else class="loading-placeholder">加载中...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useVirtualList } from '@vueuse/core'
import { useMemberStore } from '../stores/member.js'

const memberStore = useMemberStore()
const scrollContainer = ref(null)
const containerHeight = ref(500)
const activeTab = ref('all')
const selectedPrivilege = ref(null)

// 等级映射
const levelMap = {
  normal: '普通会员',
  silver: '白银会员',
  gold: '黄金会员'
}

// 权益标签页
const privilegeTabs = [
  { label: '全部权益', value: 'all' },
  { label: '普通会员', value: 'normal' },
  { label: '白银会员', value: 'silver' },
  { label: '黄金会员', value: 'gold' }
]

// 过滤后的权益
const filteredPrivileges = computed(() => {
  if (activeTab.value === 'all') {
    return memberStore.privileges
  }
  return memberStore.privileges.filter(p => p.level.includes(activeTab.value))
})

// 虚拟滚动配置
let virtualList = ref(null)

// 计算容器高度
onMounted(async () => {
  await memberStore.fetchPrivileges()
  await nextTick()
  
  // 根据窗口大小调整容器高度
  if (scrollContainer.value) {
    const container = scrollContainer.value
    const rect = container.getBoundingClientRect()
    containerHeight.value = Math.min(rect.height, 600)
    
    // 延迟初始化虚拟滚动，确保DOM元素已完全加载
    setTimeout(() => {
      virtualList = useVirtualList(filteredPrivileges, {
        itemHeight: 80,
        overscan: 5
      })
    }, 100)
  }
})

// 监听标签页变化，重置滚动位置
watch(activeTab, async () => {
  await nextTick()
  if (scrollContainer.value) {
    scrollContainer.value.scrollTop = 0
  }
})

// 显示权益详情
const showPrivilegeDetail = (privilege) => {
  selectedPrivilege.value = privilege
}
</script>

<style scoped>
/* 基本样式保持不变 */
.privilege-list {
  background-color: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
  padding-bottom: 0.5rem;
  border-bottom: 3px solid var(--color-primary);
  display: inline-block;
}

.privilege-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 0.8rem 1.5rem;
  background-color: var(--color-background-light);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--color-text-secondary);
}

.tab-btn.active {
  background-color: var(--color-primary);
  color: white;
}

.tab-btn:hover:not(.active) {
  background-color: var(--color-border);
}

.privileges-container {
  position: relative;
}

.virtual-list {
  overflow-y: auto;
  border-radius: 8px;
  border: 1px solid var(--color-border);
}

.virtual-list-content {
  position: relative;
}

.privilege-item {
  display: flex;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: background-color 0.3s ease;
  position: absolute;
  width: 100%;
  box-sizing: border-box;
}

.privilege-item:last-child {
  border-bottom: none;
}

.privilege-item:hover {
  background-color: var(--color-background-light);
}

.privilege-icon {
  font-size: 2.5rem;
  margin-right: 1.5rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 193, 7, 0.1);
  border-radius: 50%;
}

.privilege-info {
  flex: 1;
}

.privilege-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.3rem;
}

.privilege-description {
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.privilege-action {
  color: var(--color-text-secondary);
  font-size: 1.2rem;
}
</style>