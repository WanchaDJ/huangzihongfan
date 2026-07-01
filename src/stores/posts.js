import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  addPost as apiAddPost,
  deletePost as apiDeletePost,
  doPostFavour,
  doPostThumb,
  listMyFavourPostByPage,
  listMyPostVOByPage,
  listPostVOByPage
} from '../api/post'
import { addComment as apiAddComment, listCommentByPostId } from '../api/comment'
import { cancelOrder, confirmOrder, createOrder as apiCreateOrder, listMyOrders, payOrder } from '../api/order'
import { listProductByPage } from '../api/product'
import { doProductFavour, listMyFavourProductByPage } from '../api/productFavour'
import { listEventByPage } from '../api/event'
import { getUserVOById } from '../api/user'
import { resolveAvatar } from '@/utils/avatar'

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) return String(time)
  const diff = Date.now() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function normalizePost(post) {
  const user = post.user || {}
  const username = user.userName || `用户${post.userId || ''}`
  const rawContent = post.content || ''
  const images = Array.from(rawContent.matchAll(/!\[[^\]]*]\(([^)]+)\)/g)).map((match) => match[1])
  const content = rawContent.replace(/!\[[^\]]*]\([^)]+\)/g, '').trim()
  return {
    id: post.id,
    userId: post.userId,
    username,
    avatar: resolveAvatar(user, username),
    title: post.title || '分享',
    content,
    tags: post.tagList || [],
    images,
    likes: post.thumbNum || 0,
    favourNum: post.favourNum || 0,
    comments: [],
    createTime: post.createTime || '',
    time: formatTime(post.createTime),
    liked: !!post.hasThumb,
    favorited: !!post.hasFavour
  }
}

function getStatusText(status) {
  const map = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status || ''
}

function normalizeOrder(order) {
  return {
    id: order.id,
    orderNo: order.orderNo,
    time: formatTime(order.createTime),
    createTime: order.createTime,
    status: order.status,
    statusText: order.statusText || getStatusText(order.status),
    items: order.items || [],
    total: Number(order.totalAmount || 0),
    itemCount: order.itemCount || 0
  }
}

function normalizeProduct(product, favouriteIds = []) {
  return {
    id: product.id,
    name: product.name,
    title: product.name,
    description: product.description || '',
    price: Number(product.price || 0),
    originalPrice: Number(product.originalPrice || product.price || 0),
    stock: Number(product.stock || 0),
    soldCount: Number(product.soldCount || 0),
    image: product.coverImage,
    images: product.images,
    type: product.type || 'merch',
    category: product.category || '官方周边',
    favorited: favouriteIds.includes(product.id),
    isVerified: typeof product.coverImage === 'string' && product.coverImage.includes('/api/uploads/seed/')
  }
}

export const usePostStore = defineStore('posts', () => {
  const posts = ref([])
  const userPosts = ref([])
  const favoritePosts = ref([])
  const favoriteProducts = ref([])
  const cart = ref([])
  const orders = ref([])
  const products = ref([])
  const events = ref([])
  const loading = ref(false)

  async function fetchPosts() {
    const res = await listPostVOByPage({ current: 1, pageSize: 20, sortField: 'createTime', sortOrder: 'descend' })
    posts.value = (res.data?.records || []).map(normalizePost)
    return posts.value
  }

  async function fetchMyPosts() {
    const res = await listMyPostVOByPage({ current: 1, pageSize: 20, sortField: 'createTime', sortOrder: 'descend' })
    userPosts.value = (res.data?.records || []).map(normalizePost)
    return userPosts.value
  }

  async function fetchFavoritePosts() {
    const res = await listMyFavourPostByPage({ current: 1, pageSize: 20, sortField: 'createTime', sortOrder: 'descend' })
    favoritePosts.value = (res.data?.records || []).map(normalizePost)
    return favoritePosts.value
  }

  async function fetchFavoriteProducts() {
    const res = await listMyFavourProductByPage({ current: 1, pageSize: 50, sortField: 'createTime', sortOrder: 'descend' })
    favoriteProducts.value = (res.data?.records || []).map((item) => normalizeProduct(item))
    syncProductFavouriteState()
    return favoriteProducts.value
  }

  async function addPost(post) {
    const res = await apiAddPost({
      title: post.title || '分享',
      content: post.content,
      tags: post.tags || []
    })
    await Promise.allSettled([fetchPosts(), fetchMyPosts()])
    return res.data
  }

  async function deletePost(postId) {
    await apiDeletePost({ id: postId })
    await Promise.allSettled([fetchPosts(), fetchMyPosts(), fetchFavoritePosts()])
  }

  async function toggleLike(postId) {
    await doPostThumb({ postId })
    await fetchPosts()
  }

  async function toggleFavorite(postId) {
    await doPostFavour({ postId })
    await Promise.allSettled([fetchPosts(), fetchFavoritePosts()])
  }

  async function addComment(postId, content) {
    await apiAddComment({ postId, content: typeof content === 'string' ? content : content.content })
    await fetchPostComments(postId)
  }

  async function fetchPostComments(postId) {
    const res = await listCommentByPostId({ postId, current: 1, pageSize: 20 })
    const comments = await Promise.all(
      (res.data?.records || []).map(async (comment) => {
        let user = null
        try {
          user = (await getUserVOById(comment.userId)).data
        } catch {
          user = null
        }
        const username = user?.userName || `用户${comment.userId || ''}`
        return {
          id: comment.id,
          userId: comment.userId,
          username,
          avatar: resolveAvatar(user, username),
          content: comment.content,
          time: formatTime(comment.createTime)
        }
      })
    )

    ;[posts.value, userPosts.value, favoritePosts.value].forEach((list) => {
      const post = list.find((item) => item.id === postId)
      if (post) post.comments = comments
    })
    return comments
  }

  function syncProductFavouriteState() {
    const ids = new Set(favoriteProducts.value.map((item) => item.id))
    products.value = products.value.map((item) => ({
      ...item,
      favorited: ids.has(item.id)
    }))
  }

  async function fetchProducts() {
    const [productRes, favouriteRes] = await Promise.allSettled([
      listProductByPage({ current: 1, pageSize: 20, status: 1 }),
      listMyFavourProductByPage({ current: 1, pageSize: 50, sortField: 'createTime', sortOrder: 'descend' })
    ])

    const favouriteList = favouriteRes.status === 'fulfilled'
      ? (favouriteRes.value.data?.records || []).map((item) => normalizeProduct(item))
      : favoriteProducts.value
    favoriteProducts.value = favouriteList
    const favouriteIds = favouriteList.map((item) => item.id)

    const rawProducts = productRes.status === 'fulfilled'
      ? (productRes.value.data?.records || [])
      : []
    const mappedProducts = rawProducts.map((item) => normalizeProduct(item, favouriteIds))
    const verifiedProducts = mappedProducts.filter((item) => item.isVerified)
    products.value = verifiedProducts.length ? verifiedProducts : mappedProducts
    syncProductFavouriteState()
    return products.value
  }

  async function toggleProductFavorite(productId) {
    await doProductFavour({ productId })
    await Promise.allSettled([fetchFavoriteProducts(), fetchProducts()])
  }

  async function fetchEvents() {
    const res = await listEventByPage({ current: 1, pageSize: 20 })
    events.value = (res.data?.records || []).map((event) => ({
      id: event.id,
      title: event.title,
      date: event.eventDate,
      time: formatTime(event.eventDate),
      city: event.city,
      venue: event.venue,
      location: [event.city, event.venue].filter(Boolean).join(' '),
      price: Number(event.minPrice || 0),
      image: event.coverImage,
      available:
        ['ON_SALE', 'PRESALE', 'onSale'].includes(event.status) &&
        Number(event.stock || 0) > Number(event.soldCount || 0),
      stock: Math.max(0, Number(event.stock || 0) - Number(event.soldCount || 0)),
      status: event.status
    }))
    return events.value
  }

  function addToCart(item) {
    const type = item.type || 'merch'
    const existing = cart.value.find((cartItem) => cartItem.id === item.id && cartItem.type === type)
    if (existing) {
      existing.quantity += 1
      return
    }
    cart.value.push({ ...item, type, quantity: 1 })
  }

  function removeFromCart(itemId, itemType) {
    cart.value = cart.value.filter((item) => !(item.id === itemId && (!itemType || item.type === itemType)))
  }

  function updateCartQuantity(itemId, quantity, itemType) {
    const item = cart.value.find((cartItem) => cartItem.id === itemId && (!itemType || cartItem.type === itemType))
    if (item) {
      item.quantity = Math.max(1, Number(quantity) || 1)
    }
  }

  function clearCart() {
    cart.value = []
  }

  async function createOrder() {
    if (!cart.value.length) return false
    const tickets = cart.value.filter((item) => item.type === 'ticket')
    const goods = cart.value.filter((item) => item.type !== 'ticket')

    for (const ticket of tickets) {
      await apiCreateOrder({
        eventId: ticket.id,
        price: String(ticket.price || 0),
        quantity: ticket.quantity || 1,
        spec: ticket.title || '演出票'
      })
    }

    if (goods.length) {
      await apiCreateOrder({
        items: goods.map((item) => ({
          productId: item.id,
          quantity: item.quantity || 1
        }))
      })
    }

    clearCart()
    await fetchOrders()
    return true
  }

  async function createDirectProductOrder(product, quantity = 1) {
    if (!product?.id) return false
    await apiCreateOrder({
      items: [
        {
          productId: product.id,
          quantity: Math.max(1, Number(quantity) || 1)
        }
      ]
    })
    await fetchOrders()
    return true
  }

  async function fetchOrders() {
    const res = await listMyOrders({ current: 1, pageSize: 20 })
    orders.value = (res.data?.records || []).map(normalizeOrder)
    return orders.value
  }

  async function payOrderAction(orderId) {
    await payOrder(orderId)
    await fetchOrders()
  }

  async function cancelOrderAction(orderId) {
    await cancelOrder(orderId)
    await fetchOrders()
  }

  async function confirmOrderAction(orderId) {
    await confirmOrder(orderId)
    await fetchOrders()
  }

  const cartTotal = computed(() =>
    cart.value.reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1), 0)
  )

  const cartItemCount = computed(() =>
    cart.value.reduce((sum, item) => sum + Number(item.quantity || 1), 0)
  )

  return {
    posts,
    userPosts,
    favoritePosts,
    favoriteProducts,
    cart,
    orders,
    products,
    events,
    loading,
    fetchPosts,
    fetchMyPosts,
    fetchFavoritePosts,
    fetchFavoriteProducts,
    addPost,
    deletePost,
    toggleLike,
    toggleFavorite,
    addComment,
    fetchPostComments,
    fetchProducts,
    toggleProductFavorite,
    fetchEvents,
    addToCart,
    removeFromCart,
    updateCartQuantity,
    clearCart,
    createOrder,
    createDirectProductOrder,
    fetchOrders,
    payOrderAction,
    cancelOrderAction,
    confirmOrderAction,
    cartTotal,
    cartItemCount
  }
})
