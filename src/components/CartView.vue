<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/stores/posts'

const router = useRouter()
const postStore = usePostStore()
const message = ref('')

const cartItems = computed(() => postStore.cart)
const cartTotal = computed(() => postStore.cartTotal)
const cartItemCount = computed(() => postStore.cartItemCount)

function updateQuantity(item, change) {
  postStore.updateCartQuantity(item.id, item.quantity + change, item.type)
}

function removeItem(item) {
  postStore.removeFromCart(item.id, item.type)
}

function clearCart() {
  if (window.confirm('确定清空购物车吗？')) postStore.clearCart()
}

async function createOrder() {
  message.value = ''
  try {
    const ok = await postStore.createOrder()
    if (ok) {
      message.value = '订单已生成，请到“我的订单”查看并支付。'
      router.push('/profile/orders')
    }
  } catch (error) {
    message.value = error.message || '订单生成失败，请确认已登录。'
  }
}
</script>

<template>
  <div class="cart-container">
    <div class="cart-header">
      <h2>我的购物车</h2>
      <div v-if="cartItemCount > 0" class="cart-info">
        <span>{{ cartItemCount }} 件商品</span>
        <span class="cart-total">合计：¥{{ cartTotal.toFixed(2) }}</span>
      </div>
    </div>

    <p v-if="message" class="message">{{ message }}</p>

    <div v-if="cartItems.length === 0" class="cart-empty">
      <h3>购物车是空的</h3>
      <p>可以在会员中心选择实体专辑、官方应援棒等商品。</p>
      <button class="btn-primary" @click="router.push('/member-center')">去会员中心</button>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <article v-for="item in cartItems" :key="`${item.id}-${item.type}`" class="cart-item">
          <img :src="item.image" :alt="item.title || item.name" />
          <div class="item-info">
            <h3>{{ item.title || item.name }}</h3>
            <p>{{ item.category || item.type }}</p>
            <strong>¥{{ Number(item.price || 0).toFixed(2) }}</strong>
          </div>
          <div class="quantity">
            <button @click="updateQuantity(item, -1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="updateQuantity(item, 1)">+</button>
          </div>
          <strong class="subtotal">¥{{ (Number(item.price || 0) * item.quantity).toFixed(2) }}</strong>
          <button class="remove-btn" @click="removeItem(item)">删除</button>
        </article>
      </div>

      <div class="cart-footer">
        <button class="btn-outline" @click="clearCart">清空购物车</button>
        <button class="btn-outline" @click="router.push('/member-center')">继续购物</button>
        <div class="spacer"></div>
        <strong>总计：¥{{ cartTotal.toFixed(2) }}</strong>
        <button class="btn-primary" @click="createOrder">去结算</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-header,
.cart-footer,
.quantity {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-header {
  justify-content: space-between;
  margin-bottom: 20px;
}

.cart-header h2 {
  color: var(--color-heading);
  font-size: 1.45rem;
}

.cart-info {
  display: flex;
  gap: 18px;
  color: var(--color-text-secondary);
}

.cart-total,
.subtotal,
.cart-footer strong {
  color: var(--color-primary-dark);
}

.message {
  padding: 12px 14px;
  margin-bottom: 16px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.cart-empty {
  text-align: center;
  padding: 64px 0;
  color: var(--color-text-secondary);
}

.cart-empty h3 {
  color: var(--color-heading);
  margin-bottom: 8px;
}

.cart-empty p {
  margin-bottom: 18px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cart-item {
  display: grid;
  grid-template-columns: 84px 1fr 116px 110px 74px;
  gap: 14px;
  align-items: center;
  padding: 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.cart-item img {
  width: 84px;
  height: 84px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.item-info h3 {
  color: var(--color-heading);
  font-size: 1rem;
  margin-bottom: 4px;
}

.item-info p {
  color: var(--color-text-secondary);
  font-size: 0.84rem;
}

.item-info strong {
  display: block;
  margin-top: 6px;
  color: var(--color-primary-dark);
}

.quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid var(--color-border);
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
}

.quantity span {
  min-width: 28px;
  text-align: center;
  font-weight: 800;
}

.remove-btn {
  padding: 8px 10px;
  border: 1px solid var(--color-danger);
  border-radius: var(--radius-md);
  background: #fff;
  color: var(--color-danger);
  cursor: pointer;
}

.cart-footer {
  margin-top: 22px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
}

.spacer {
  flex: 1;
}

@media (max-width: 760px) {
  .cart-item {
    grid-template-columns: 72px 1fr;
  }

  .quantity,
  .subtotal,
  .remove-btn {
    grid-column: 2;
  }

  .cart-footer {
    align-items: stretch;
    flex-direction: column;
  }

  .spacer {
    display: none;
  }
}
</style>
