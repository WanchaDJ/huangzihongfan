import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  },
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/about', name: 'about', component: () => import('../views/AboutView.vue') },
    { path: '/gallery', name: 'gallery', component: () => import('../views/GalleryView.vue') },
    { path: '/news', name: 'news', component: () => import('../views/NewsView.vue') },
    { path: '/contact', name: 'contact', component: () => import('../views/ContactView.vue') },
    { path: '/album', name: 'album', component: () => import('../views/AlbumView.vue') },
    { path: '/music-album', name: 'music-album', component: () => import('../views/MusicAlbumView.vue') },
    { path: '/posts', name: 'posts', component: () => import('../views/PostView.vue') },
    { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue') },
    { path: '/test', name: 'test', component: () => import('../views/TestImageView.vue') },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'profile-basic', component: () => import('../components/UserProfile.vue') },
        { path: 'edit', name: 'profile-edit', component: () => import('../components/ProfileEditor.vue') },
        { path: 'orders', name: 'profile-orders', component: () => import('../components/OrderList.vue') },
        { path: 'cart', name: 'profile-cart', component: () => import('../components/CartView.vue') },
        { path: 'my-posts', name: 'profile-my-posts', component: () => import('../components/MyPostsView.vue') },
        { path: 'favorites', name: 'profile-favorites', component: () => import('../components/FavoriteListView.vue') },
        { path: 'security', name: 'profile-security', component: () => import('../components/SecuritySettingsView.vue') }
      ]
    },
    { path: '/tickets', name: 'tickets', component: () => import('../views/TicketView.vue') },
    { path: '/member-center', name: 'member-center', component: () => import('../views/MemberCenterView.vue'), meta: { requiresAuth: true } },
    { path: '/ai-chat', name: 'ai-chat', component: () => import('../views/AIChatView.vue'), meta: { requiresAuth: true } },
    { path: '/chat', name: 'chat', component: () => import('../views/ChatView.vue'), meta: { requiresAuth: true } }
  ]
})

router.beforeEach((to) => {
  if (!to.matched.some((record) => record.meta.requiresAuth)) return true
  const user = localStorage.getItem('auth_user')
  if (!user) return { path: '/login', query: { redirect: to.fullPath } }
  return true
})

export default router
