<template>
  <div class="membership-card">
    <div class="card-header" :style="{ backgroundColor: memberStore.levelColor }">
      <div class="member-avatar">
        <span class="avatar">👤</span>
      </div>
      <div class="member-info">
        <h2 class="member-name">{{ memberStore.memberInfo.username }}</h2>
        <div class="member-level" :style="{ color: memberStore.levelColor }">
          {{ memberStore.memberInfo.level }}
        </div>
      </div>
    </div>
    
    <div class="card-content">
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">会员到期</div>
          <div class="info-value">
            {{ memberStore.memberInfo.expireDate }}
            <div class="countdown">
              剩余 {{ memberStore.remainingDays }} 天
            </div>
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">成长值</div>
          <div class="info-value">
            {{ memberStore.memberInfo.growthValue }}
            <div class="progress-bar">
              <div class="progress" :style="{ width: growthProgress + '%' }"></div>
            </div>
            <div class="next-level">
              距离下一级还需 {{ nextLevelGrowth - memberStore.memberInfo.growthValue }} 成长值
            </div>
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">总消费</div>
          <div class="info-value">¥{{ memberStore.memberInfo.totalSpend }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">积分</div>
          <div class="info-value">{{ memberStore.memberInfo.points }}</div>
        </div>
      </div>
    </div>
    
    <div class="card-footer">
      <router-link to="/member-center/renew" class="renew-btn">
        立即续费
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useMemberStore } from '../stores/member.js'

const memberStore = useMemberStore()

// 计算成长值进度
const growthProgress = computed(() => {
  const value = memberStore.memberInfo.growthValue
  if (value < 500) {
    return (value / 500) * 100
  } else if (value < 1500) {
    return 100 + ((value - 500) / 1000) * 100
  } else {
    return 200
  }
})

// 计算下一级所需成长值
const nextLevelGrowth = computed(() => {
  const value = memberStore.memberInfo.growthValue
  if (value < 500) {
    return 500
  } else if (value < 1500) {
    return 1500
  } else {
    return memberStore.memberInfo.growthValue + 1000
  }
})

onMounted(() => {
  memberStore.fetchMemberInfo()
})
</script>

<style scoped>
.membership-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.card-header {
  padding: 2rem;
  color: white;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.member-avatar {
  width: 100px;
  height: 100px;
  margin: 0 auto 1rem;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  font-size: 4rem;
}

.member-name {
  font-size: 1.5rem;
  margin: 0 0 0.5rem;
}

.member-level {
  font-size: 1.2rem;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 0.3rem 1rem;
  border-radius: 20px;
  display: inline-block;
}

.card-content {
  padding: 2rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.info-item {
  text-align: center;
}

.info-label {
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  margin-bottom: 0.5rem;
}

.info-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.countdown {
  font-size: 0.9rem;
  color: var(--color-primary);
  margin-top: 0.3rem;
}

.progress-bar {
  height: 4px;
  background-color: var(--color-border);
  border-radius: 2px;
  margin: 0.5rem 0;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: var(--color-primary);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.next-level {
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}

.card-footer {
  padding: 1rem 2rem;
  background-color: var(--color-background-light);
  text-align: center;
}

.renew-btn {
  display: inline-block;
  padding: 0.8rem 2rem;
  background-color: var(--color-primary);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.renew-btn:hover {
  background-color: var(--color-primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.6);
}

/* Responsive */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    padding: 1.5rem;
  }
  
  .card-content {
    padding: 1.5rem;
  }
}
</style>