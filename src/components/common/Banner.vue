<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

// 导入轮播图片
import img1 from '../../assets/黄子弘凡/webwxgetmsgimg.jpg'
import img2 from '../../assets/黄子弘凡/webwxgetmsgimg (3).jpg'
import img3 from '../../assets/黄子弘凡/webwxgetmsgimg (4).jpg'
import img4 from '../../assets/黄子弘凡/演唱会现场.jpg'
import img5 from '../../assets/黄子弘凡/活动现场.jpg'

const router = useRouter()

// 轮播图片数据
const carouselImages = [
  { src: img1, alt: '黄子弘凡照片1' },
  { src: img2, alt: '黄子弘凡照片2' },
  { src: img3, alt: '黄子弘凡照片3' },
  { src: img4, alt: '黄子弘凡照片4' },
  { src: img5, alt: '黄子弘凡照片5' }
]

const currentIndex = ref(0)
let carouselTimer = null

// 切换到下一张图片
function nextSlide() {
  currentIndex.value = (currentIndex.value + 1) % carouselImages.length
}

// 开始轮播
function startCarousel() {
  carouselTimer = setInterval(nextSlide, 3000)
}

// 停止轮播
function stopCarousel() {
  if (carouselTimer) {
    clearInterval(carouselTimer)
    carouselTimer = null
  }
}

// 导航函数
function navigateToPage(page) {
  router.push(`/${page}`)
}

// 生命周期钩子
onMounted(() => {
  startCarousel()
})

onUnmounted(() => {
  stopCarousel()
})
</script>

<template>
  <section class="banner-section" @mouseenter="stopCarousel" @mouseleave="startCarousel">
    <!-- 轮播图片容器 -->
    <div class="carousel-container">
      <TransitionGroup name="carousel" tag="div" class="carousel-slide">
        <div 
          v-for="(image, index) in carouselImages" 
          :key="index" 
          class="carousel-item"
          :style="{ 
            position: index === currentIndex ? 'relative' : 'absolute',
            opacity: index === currentIndex ? 1 : 0,
            transform: `translateX(${index === currentIndex ? 0 : index > currentIndex ? '100%' : '-100%'})`
          }"
        >
          <img :src="image.src" :alt="image.alt" />
          <div class="carousel-overlay"></div>
        </div>
      </TransitionGroup>
    </div>
    
    <!-- 轮播控制指示器 -->
    <div class="carousel-indicators">
      <span 
        v-for="(image, index) in carouselImages" 
        :key="index" 
        class="indicator" 
        :class="{ active: index === currentIndex }"
        @click="currentIndex = index"
      ></span>
    </div>
    
    <!-- Banner内容 -->
    <div class="banner-content">
      <h1 class="banner-title">黄子弘凡</h1>
      <p class="banner-description">实力唱将 · 舞台王者</p>
      <div class="banner-buttons">
        <button class="btn btn-primary" @click="navigateToPage('gallery')">浏览相册</button>
        <button class="btn btn-secondary" @click="navigateToPage('about')">了解更多</button>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* Banner区域样式 */
.banner-section {
  min-height: calc(100vh - 80px); /* 减去导航栏高度 */
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 轮播容器样式 */
.carousel-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.carousel-slide {
  display: flex;
  width: 100%;
  height: 100%;
}

.carousel-item {
  min-width: 100%;
  height: 100%;
  position: relative;
  transition: all 0.5s ease-in-out;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7));
  z-index: 1;
}

/* 轮播指示器样式 */
.carousel-indicators {
  position: absolute;
  bottom: 30px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 10px;
  z-index: 2;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator.active {
  background-color: #ffb900;
  transform: scale(1.2);
}

/* Banner内容样式 */
.banner-content {
  text-align: center;
  color: white;
  z-index: 1;
  max-width: 800px;
  padding: 40px 20px;
}

.banner-title {
  font-size: 4rem;
  font-weight: 900;
  margin-bottom: 20px;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
  background: linear-gradient(45deg, #ffffff, #ffb900);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.banner-description {
  font-size: 1.5rem;
  margin-bottom: 30px;
  opacity: 0.9;
}

.banner-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

/* 按钮样式 */
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: #ffb900;
  color: #000;
}

.btn-primary:hover {
  background: #ffc837;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 185, 0, 0.4);
}

.btn-secondary {
  background: transparent;
  border: 2px solid white;
  color: white;
}

.btn-secondary:hover {
  background: white;
  color: #000;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-title {
    font-size: 2.5rem;
  }
  
  .banner-description {
    font-size: 1.2rem;
  }
  
  .banner-section {
    min-height: calc(80vh - 80px);
  }
}

@media (max-width: 480px) {
  .banner-title {
    font-size: 2.5rem;
  }
  
  .banner-description {
    font-size: 1.2rem;
  }
}
</style>