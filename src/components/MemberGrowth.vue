<template>
  <div class="member-growth">
    <!-- 成长值趋势图 -->
    <div v-if="showSections.growthChart" class="growth-chart-section">
      <h2 class="section-title">会员成长趋势</h2>
      <div ref="growthChart" class="growth-chart"></div>
    </div>
    
    <!-- 历史消费记录 -->
    <div v-if="showSections.consumption" class="consumption-section">
      <h2 class="section-title">历史消费记录</h2>
      <div class="records-container">
        <div 
          ref="consumptionScrollContainer"
          class="virtual-list"
          :style="{ height: `${containerHeight}px` }"
        >
          <div 
            class="virtual-list-content"
            :style="{ height: consumptionVirtualList?.totalHeight || 0 + 'px' }"
          >
            <div
              v-if="consumptionVirtualList?.visibleItems"
              v-for="virtualItem in consumptionVirtualList.visibleItems"
              :key="virtualItem.data.id"
              class="record-item"
              :style="{
                transform: `translateY(${virtualItem.start}px)`,
                height: `${virtualItem.size}px`
              }"
            >
              <div class="record-date">{{ virtualItem.data.date }}</div>
              <div class="record-info">
                <div class="record-description">{{ virtualItem.data.description }}</div>
                <div class="record-amount" :class="{ positive: virtualItem.data.amount >= 0 }">
                  {{ virtualItem.data.amount >= 0 ? '+' : '' }}{{ virtualItem.data.amount }}元
                </div>
              </div>
            </div>
            <div v-else class="loading-placeholder">加载中...</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 积分明细 -->
    <div v-if="showSections.points" class="points-section">
      <h2 class="section-title">积分明细</h2>
      <div class="points-summary">
        <div class="points-total">
          <span class="label">当前积分:</span>
          <span class="value">{{ memberStore.memberInfo.points }}</span>
        </div>
        <div class="points-level">
          <span class="label">距离下一等级还差:</span>
          <span class="value">{{ memberStore.nextLevelGrowth - memberStore.memberInfo.growthValue }}成长值</span>
        </div>
      </div>
      <div class="records-container">
        <div 
          ref="pointsScrollContainer"
          class="virtual-list"
          :style="{ height: `${containerHeight}px` }"
        >
          <div 
            class="virtual-list-content"
            :style="{ height: pointsVirtualList?.totalHeight || 0 + 'px' }"
          >
            <div
              v-if="pointsVirtualList?.visibleItems"
              v-for="virtualItem in pointsVirtualList.visibleItems"
              :key="virtualItem.data.id"
              class="record-item"
              :style="{
                transform: `translateY(${virtualItem.start}px)`,
                height: `${virtualItem.size}px`
              }"
            >
              <div class="record-date">{{ virtualItem.data.date }}</div>
              <div class="record-info">
                <div class="record-description">{{ virtualItem.data.description }}</div>
                <div class="record-amount" :class="{ positive: virtualItem.data.points >= 0 }">
                  {{ virtualItem.data.points >= 0 ? '+' : '' }}{{ virtualItem.data.points }}积分
                </div>
              </div>
            </div>
            <div v-else class="loading-placeholder">加载中...</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 会员推荐功能 -->
    <div v-if="showSections.referral" class="referral-section">
      <h2 class="section-title">会员推荐</h2>
      <div class="referral-card">
        <div class="referral-info">
          <div class="referral-title">邀请好友加入会员</div>
          <div class="referral-benefits">
            <div class="benefit-item">
              <span class="benefit-icon">🎁</span>
              <span class="benefit-text">好友成功注册可获得100积分</span>
            </div>
            <div class="benefit-item">
              <span class="benefit-icon">💎</span>
              <span class="benefit-text">好友消费时您可获得5%的积分奖励</span>
            </div>
          </div>
        </div>
        
        <div class="referral-link-container">
          <div class="referral-link">
            <input 
              type="text" 
              :value="referralLink" 
              readonly 
              class="link-input"
            >
            <button class="copy-btn" @click="copyReferralLink">复制链接</button>
          </div>
          <div class="share-options">
            <button class="share-btn" @click="shareToWeChat">
              <span class="icon">💬</span> 微信分享
            </button>
            <button class="share-btn" @click="shareToQQ">
              <span class="icon">🐧</span> QQ分享
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useVirtualList } from '@vueuse/core'
import { useMemberStore } from '../stores/member.js'

// 懒加载 echarts
let echarts = null
const loadEcharts = async () => {
  if (!echarts) {
    const module = await import('echarts')
    echarts = module.default
  }
  return echarts
}

// 定义显示控制props
defineProps({
  showSections: {
    type: Object,
    default: () => ({
      growthChart: true,
      consumption: true,
      points: true,
      referral: true
    })
  }
})

const memberStore = useMemberStore()
const growthChart = ref(null)
let chartInstance = null

// 容器高度配置
const containerHeight = ref(400)

// 消费记录虚拟滚动
const consumptionScrollContainer = ref(null)
const consumptionVirtualList = ref(null)

// 积分记录虚拟滚动
const pointsScrollContainer = ref(null)
const pointsVirtualList = ref(null)

// 推荐链接
const referralLink = computed(() => {
  return `${window.location.origin}/register?invite=${memberStore.memberInfo?.inviteCode || ''}`
})

// 更新成长值趋势图
const updateGrowthChart = () => {
  if (!chartInstance || !memberStore.growthHistory.length) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}成长值'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: memberStore.growthHistory.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      name: '成长值'
    },
    series: [
      {
        name: '成长值',
        type: 'line',
        data: memberStore.growthHistory.map(item => item.value),
        smooth: true,
        itemStyle: {
          color: '#4CAF50'
        },
        areaStyle: {
          color: new chartInstance.getEcharts().graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(76, 175, 80, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(76, 175, 80, 0.1)'
            }
          ])
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

// 初始化图表
const initChart = async () => {
  if (!growthChart.value) return
  
  try {
    const echartsInstance = await loadEcharts()
    chartInstance = echartsInstance.init(growthChart.value)
    updateGrowthChart()
  } catch (e) {
    console.error('初始化图表失败:', e)
  }
}

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 复制推荐链接
const copyReferralLink = () => {
  navigator.clipboard.writeText(referralLink.value)
    .then(() => {
      alert('邀请链接已复制到剪贴板！')
    })
    .catch(err => {
      console.error('复制失败:', err)
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = referralLink.value
      document.body.appendChild(textArea)
      textArea.select()
      document.execCommand('copy')
      document.body.removeChild(textArea)
      alert('邀请链接已复制到剪贴板！')
    })
}

// 分享功能
const shareToWeChat = () => {
  alert('微信分享功能正在开发中...')
}

const shareToQQ = () => {
  alert('QQ分享功能正在开发中...')
}

// 监听成长历史数据变化，更新图表
watch(
  () => memberStore.growthHistory,
  (newHistory) => {
    if (newHistory.length > 0) {
      updateGrowthChart()
    }
  },
  { deep: true }
)

// 生命周期钩子
onMounted(async () => {
  try {
    // 串行加载数据，避免同时发起多个请求
    await memberStore.fetchConsumptionHistory()
    
    // 只有在需要显示时才加载数据
    if (showSections.value.growthChart) {
      await memberStore.fetchGrowthHistory()
      await initChart()
    }
    
    if (showSections.value.points) {
      await memberStore.fetchPointsHistory()
    }
    
    // 延迟初始化虚拟滚动，确保DOM元素已完全加载
    setTimeout(() => {
      if (consumptionScrollContainer.value && showSections.value.consumption) {
        consumptionVirtualList.value = useVirtualList(
          memberStore.consumptionHistory,
          { itemHeight: 80, overscan: 5 }
        )
      }
      
      if (pointsScrollContainer.value && showSections.value.points) {
        pointsVirtualList.value = useVirtualList(
          memberStore.pointsHistory,
          { itemHeight: 80, overscan: 5 }
        )
      }
    }, 100)
  } catch (e) {
    console.error('加载会员数据失败:', e)
  }
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.member-growth {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

.growth-chart-section, .consumption-section, .points-section, .referral-section {
  background-color: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
  padding-bottom: 0.5rem;
  border-bottom: 3px solid var(--color-primary);
  display: inline-block;
}

/* 成长趋势图 */
.growth-chart {
  width: 100%;
  height: 400px;
}

/* 消费记录和积分明细 */
.records-container {
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

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  position: absolute;
  width: 100%;
  box-sizing: border-box;
}

.record-item:last-child {
  border-bottom: none;
}

.record-date {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  margin-right: 2rem;
}

.record-info {
  flex: 1;
}

.record-description {
  font-size: 1.1rem;
  color: var(--color-text-primary);
  margin-bottom: 0.3rem;
}

.record-amount {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ff4d4f;
}

.record-amount.positive {
  color: #52c41a;
}

/* 积分汇总 */
.points-summary {
  display: flex;
  gap: 3rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background-color: var(--color-background-light);
  border-radius: 8px;
  flex-wrap: wrap;
}

.points-total, .points-level {
  display: flex;
  flex-direction: column;
}

.points-summary .label {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.points-summary .value {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--color-primary);
}

/* 推荐功能 */
.referral-card {
  background-color: var(--color-background-light);
  border-radius: 12px;
  padding: 2rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  align-items: center;
}

.referral-info {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.referral-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.referral-benefits {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 1.1rem;
}

.benefit-item .icon {
  font-size: 1.5rem;
}

.referral-stats {
  display: flex;
  gap: 3rem;
  padding: 1.5rem 0;
  border-top: 1px solid var(--color-border);
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--color-primary);
}

.referral-link-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.referral-link {
  display: flex;
  gap: 1rem;
}

.link-input {
  flex: 1;
  padding: 1rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 1rem;
  color: var(--color-text-primary);
  background-color: white;
}

.copy-btn {
  padding: 1rem 2rem;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s ease;
}

.copy-btn:hover {
  background-color: var(--color-primary-dark);
}

.share-options {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.share-btn {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 1rem 1.5rem;
  background-color: white;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.share-btn:hover {
  background-color: var(--color-background-light);
  border-color: var(--color-primary);
}

/* Responsive */
@media (max-width: 1024px) {
  .referral-card {
    grid-template-columns: 1fr;
  }
  
  .referral-stats {
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .growth-chart-section, .consumption-section, .points-section, .referral-section {
    padding: 1.5rem;
  }
  
  .growth-chart {
    height: 300px;
  }
  
  .points-summary {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .referral-stats {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .referral-link {
    flex-direction: column;
  }
  
  .share-options {
    flex-direction: column;
  }
}
</style>
