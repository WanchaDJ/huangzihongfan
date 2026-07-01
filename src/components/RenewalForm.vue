<template>
  <div class="renewal-form">
    <h2 class="section-title">会员续费</h2>
    
    <div class="current-membership-info">
      <div class="info-item">
        <span class="label">当前会员等级:</span>
        <span class="value" :class="`level-${memberStore.memberInfo.level}`">
          {{ memberStore.levelText }}
        </span>
      </div>
      <div class="info-item">
        <span class="label">剩余有效期:</span>
        <span class="value countdown">
          <span v-if="remainingTime.days > 0">{{ remainingTime.days }}天</span>
          <span>{{ remainingTime.hours }}小时</span>
          <span>{{ remainingTime.minutes }}分钟</span>
          <span>{{ remainingTime.seconds }}秒</span>
        </span>
      </div>
      <div class="info-item">
        <span class="label">到期时间:</span>
        <span class="value">{{ memberStore.memberInfo.expireDate }}</span>
      </div>
    </div>
    
    <div class="renewal-options">
      <h3 class="option-title">选择续费时长</h3>
      <div class="duration-options">
        <label 
          v-for="option in durationOptions" 
          :key="option.duration"
          class="duration-option"
        >
          <input 
            type="radio" 
            v-model="selectedDuration" 
            :value="option.duration"
            class="duration-radio"
          >
          <div class="option-content">
            <div class="option-name">{{ option.name }}</div>
            <div class="option-price">
              <span class="original-price" v-if="option.originalPrice">
                ¥{{ option.originalPrice }}
              </span>
              <span class="current-price">¥{{ option.price }}</span>
              <span class="discount" v-if="option.discount">
                {{ option.discount }}
              </span>
            </div>
          </div>
        </label>
      </div>
    </div>
    
    <div class="payment-options">
      <h3 class="option-title">选择支付方式</h3>
      <div class="payment-methods">
        <label 
          v-for="method in paymentMethods" 
          :key="method.id"
          class="payment-method"
        >
          <input 
            type="radio" 
            v-model="selectedPayment" 
            :value="method.id"
            class="payment-radio"
          >
          <div class="method-content">
            <div class="method-icon">{{ method.icon }}</div>
            <div class="method-name">{{ method.name }}</div>
          </div>
        </label>
      </div>
    </div>
    
    <div class="order-summary">
      <div class="summary-item">
        <span class="label">会员等级:</span>
        <span class="value">{{ memberStore.levelText }}</span>
      </div>
      <div class="summary-item">
        <span class="label">续费时长:</span>
        <span class="value">{{ selectedDuration }}个月</span>
      </div>
      <div class="summary-item">
        <span class="label">原价:</span>
        <span class="value original">
          ¥{{ getOriginalPrice(selectedDuration) }}
        </span>
      </div>
      <div class="summary-item">
        <span class="label">优惠金额:</span>
        <span class="value discount">
          -¥{{ getOriginalPrice(selectedDuration) - getCurrentPrice(selectedDuration) }}
        </span>
      </div>
      <div class="summary-item total">
        <span class="label">应付金额:</span>
        <span class="value">¥{{ getCurrentPrice(selectedDuration) }}</span>
      </div>
    </div>
    
    <div class="form-actions">
      <button 
        class="renew-btn"
        @click="submitRenewal"
        :disabled="isSubmitting"
      >
        {{ isSubmitting ? '处理中...' : '立即续费' }}
      </button>
    </div>
    
    <!-- 支付成功弹窗 -->
    <div v-if="showSuccessModal" class="success-modal" @click="closeSuccessModal">
      <div class="modal-content" @click.stop>
        <div class="success-icon">🎉</div>
        <h3>续费成功！</h3>
        <p>您的会员有效期已延长至 {{ newExpireDate }}</p>
        <button class="confirm-btn" @click="closeSuccessModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useMemberStore } from '../stores/member.js'

const memberStore = useMemberStore()

// 续费选项
const durationOptions = [
  { duration: 1, name: '1个月', price: 30, originalPrice: 30 },
  { duration: 3, name: '3个月', price: 80, originalPrice: 90, discount: '9折优惠' },
  { duration: 12, name: '12个月', price: 300, originalPrice: 360, discount: '8.3折优惠' }
]

// 支付方式
const paymentMethods = [
  { id: 'alipay', name: '支付宝', icon: '🛒' },
  { id: 'wechat', name: '微信支付', icon: '💳' },
  { id: 'credit', name: '信用卡', icon: '💎' }
]

// 表单数据
const selectedDuration = ref(1)
const selectedPayment = ref('alipay')
const isSubmitting = ref(false)
const showSuccessModal = ref(false)
const newExpireDate = ref('')

// 计算剩余时间（从store获取）
const remainingTime = computed(() => memberStore.remainingTime)

// 获取原价
const getOriginalPrice = (duration) => {
  const option = durationOptions.find(opt => opt.duration === duration)
  return option ? option.originalPrice || option.price : 0
}

// 获取当前价格
const getCurrentPrice = (duration) => {
  const option = durationOptions.find(opt => opt.duration === duration)
  return option ? option.price : 0
}

// 提交续费
const submitRenewal = async () => {
  if (!selectedPayment.value) {
    alert('请选择支付方式')
    return
  }
  
  isSubmitting.value = true
  
  try {
    // 模拟支付处理
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 计算新的到期日期
    const currentExpire = new Date(memberStore.memberInfo.expireDate)
    const newExpire = new Date(currentExpire)
    newExpire.setMonth(newExpire.getMonth() + selectedDuration.value)
    
    // 格式化新的到期日期
    newExpireDate.value = newExpire.toLocaleDateString('zh-CN')
    
    // 更新会员信息
    await memberStore.renewMembership(selectedDuration.value, newExpireDate.value)
    
    // 显示成功弹窗
    showSuccessModal.value = true
  } catch (error) {
    console.error('续费失败:', error)
    alert('续费失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 关闭成功弹窗
const closeSuccessModal = () => {
  showSuccessModal.value = false
}

// 生命周期钩子
onMounted(() => {
  // 初始化时获取会员信息
  memberStore.fetchMemberInfo()
})
</script>

<style scoped>
.renewal-form {
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

/* 当前会员信息 */
.current-membership-info {
  display: flex;
  gap: 3rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background-color: var(--color-background-light);
  border-radius: 8px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-item .label {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}

.info-item .value {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.countdown {
  color: var(--color-primary);
  font-family: monospace;
}

.level-normal {
  color: #999;
}

.level-silver {
  color: #c0c0c0;
}

.level-gold {
  color: #ffd700;
}

/* 续费选项 */
.renewal-options {
  margin-bottom: 2rem;
}

.option-title {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
}

.duration-options {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.duration-option {
  position: relative;
  cursor: pointer;
}

.duration-radio {
  position: absolute;
  opacity: 0;
}

.option-content {
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 2rem;
  width: 200px;
  text-align: center;
  transition: all 0.3s ease;
}

.duration-radio:checked + .option-content {
  border-color: var(--color-primary);
  background-color: var(--color-background-light);
  box-shadow: 0 0 0 3px rgba(255, 115, 0, 0.1);
}

.option-name {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.option-price {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.original-price {
  color: var(--color-text-secondary);
  text-decoration: line-through;
  font-size: 0.9rem;
}

.current-price {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--color-primary);
}

.discount {
  background-color: var(--color-primary);
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  display: inline-block;
  align-self: center;
}

/* 支付方式 */
.payment-options {
  margin-bottom: 2rem;
}

.payment-methods {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.payment-method {
  position: relative;
  cursor: pointer;
}

.payment-radio {
  position: absolute;
  opacity: 0;
}

.method-content {
  border: 2px solid var(--color-border);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all 0.3s ease;
}

.payment-radio:checked + .method-content {
  border-color: var(--color-primary);
  background-color: var(--color-background-light);
}

.method-icon {
  font-size: 2rem;
}

.method-name {
  font-size: 1.2rem;
  color: var(--color-text-primary);
}

/* 订单摘要 */
.order-summary {
  background-color: var(--color-background-light);
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.summary-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.summary-item .label {
  color: var(--color-text-secondary);
  font-size: 1rem;
}

.summary-item .value {
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 600;
}

.summary-item.total .label {
  font-size: 1.3rem;
  color: var(--color-text-primary);
}

.summary-item.total .value {
  font-size: 1.8rem;
  color: var(--color-primary);
}

.original {
  text-decoration: line-through;
  color: var(--color-text-secondary);
}

.discount {
  color: #52c41a;
}

/* 表单操作 */
.form-actions {
  text-align: center;
}

.renew-btn {
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 1.2rem 4rem;
  font-size: 1.3rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.renew-btn:hover:not(:disabled) {
  background-color: var(--color-primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 115, 0, 0.3);
}

.renew-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 成功弹窗 */
.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.success-icon {
  font-size: 5rem;
  margin-bottom: 1.5rem;
}

.modal-content h3 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.modal-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: var(--color-text-secondary);
}

.confirm-btn {
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-btn:hover {
  background-color: var(--color-primary-dark);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .renewal-form {
    padding: 1.5rem;
  }
  
  .current-membership-info {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .duration-options {
    flex-direction: column;
    align-items: center;
  }
  
  .option-content {
    width: 100%;
    max-width: 300px;
  }
  
  .payment-methods {
    flex-direction: column;
    align-items: center;
  }
  
  .method-content {
    width: 100%;
    max-width: 300px;
    justify-content: center;
  }
  
  .renew-btn {
    width: 100%;
    max-width: 300px;
  }
}
</style>