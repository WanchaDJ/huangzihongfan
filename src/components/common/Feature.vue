<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 内容数据
const popularSongs = [
  { id: 1, title: '《惊虹》', cover: '/src/assets/黄子弘凡/惊虹.jpg' },
  { id: 2, title: '《成为你想成为的大人》', cover: '/src/assets/黄子弘凡/成为你想成为的大人.jpg' },
  { id: 3, title: '《除了快乐禁止入内》', cover: '/src/assets/黄子弘凡/除了快乐禁止入内.jpg' },
  { id: 4, title: '《Do U》', cover: '/src/assets/黄子弘凡/Do U.jpg' }
]

const latestNews = [
  { id: 1, title: '2024全国巡回演唱会即将开启', date: '2024.05.15', image: '/src/assets/黄子弘凡/2024全国巡回演唱会即将开启.jpg' },
  { id: 2, title: '2025除了快乐禁止入内演唱会', date: '2024.03.15', image: '/src/assets/黄子弘凡/2025除了快乐禁止入内演唱会.jpg' },
  { id: 3, title: '时尚杂志专访上线', date: '2024.02.20', image: '/src/assets/黄子弘凡/时尚杂志专访上线.png' }
]

const upcomingEvents = [
  { id: 1, city: '北京', venue: '北京工人体育馆', date: '2024-06-15' },
  { id: 2, city: '上海', venue: '上海梅赛德斯奔驰文化中心', date: '2024-07-10' },
  { id: 3, city: '广州', venue: '广州体育馆', date: '2024-08-05' },
  { id: 4, city: '成都', venue: '成都露天音乐公园', date: '2024-09-20' }
]

// 导航函数
function navigateToPage(page) {
  router.push(`/${page}`)
}
</script>

<template>
  <div class="feature-container">
    <!-- z型布局的主要内容区域 -->
    <div class="z-layout-container">
      <!-- 第一行：简介和数据 -->
      <section class="z-row z-row-1">
        <div class="z-col z-col-left">
          <h2>关于黄子弘凡</h2>
          <p>黄子弘凡是中国内地新生代实力唱将，拥有独特的嗓音和出色的舞台表现力。自出道以来，凭借其精湛的唱功和迷人的舞台魅力，迅速获得了众多粉丝的喜爱和认可。</p>
          <p>他的音乐风格多元，从流行到摇滚，从抒情到动感，都能完美驾驭，展现出非凡的音乐才华。</p>
          <button class="btn btn-outline" @click="navigateToPage('about')">查看详情</button>
        </div>
        <div class="z-col z-col-right">
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-value">5M+</div>
              <div class="stat-label">粉丝数量</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">20+</div>
              <div class="stat-label">音乐作品</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">50+</div>
              <div class="stat-label">舞台演出</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">10+</div>
              <div class="stat-label">音乐奖项</div>
            </div>
          </div>
        </div>
      </section>
      
      <!-- 第二行：热门歌曲 -->
      <section class="z-row z-row-2">
        <h2 class="section-title">热门歌曲</h2>
        <div class="songs-grid">
          <div v-for="song in popularSongs" :key="song.id" class="song-card">
            <img :src="song.cover" :alt="song.title" class="song-cover">
            <h3 class="song-title">{{ song.title }}</h3>
          </div>
        </div>
        <button class="btn btn-primary" @click="navigateToPage('music-album')">查看全部作品</button>
      </section>
      
      <!-- 第三行：最新动态 -->
      <section class="z-row z-row-3">
        <h2 class="section-title">最新动态</h2>
        <div class="news-container">
          <div v-for="(item, index) in latestNews" :key="item.id" class="news-item">
            <div class="news-image">
              <img :src="item.image" :alt="item.title">
            </div>
            <div class="news-content">
              <div class="news-date">{{ item.date }}</div>
              <h3 class="news-title">{{ item.title }}</h3>
            </div>
          </div>
        </div>
        <button class="btn btn-outline" @click="navigateToPage('news')">更多动态</button>
      </section>
      
      <!-- 第四行：巡演日程 -->
      <section class="z-row z-row-4">
        <h2 class="section-title">巡演日程</h2>
        <div class="events-container">
          <div v-for="event in upcomingEvents" :key="event.id" class="event-item">
            <div class="event-date">{{ event.date }}</div>
            <div class="event-info">
              <div class="event-city">{{ event.city }}</div>
              <div class="event-venue">{{ event.venue }}</div>
            </div>
            <button class="btn btn-sm btn-primary">购票</button>
          </div>
        </div>
      </section>
      
      <!-- 第五行：粉丝互动 -->
      <section class="z-row z-row-5">
        <div class="z-col z-col-left">
          <h2>粉丝心声</h2>
          <p>你的音乐给了我前行的力量，期待更多精彩作品！</p>
          <p class="quote-author">— 小雨</p>
          <p>每场演唱会都让人难忘，永远支持你！</p>
          <p class="quote-author">— 星辰</p>
          <p>你的声音总是能治愈我，谢谢你的坚持！</p>
          <p class="quote-author">— 阳光</p>
        </div>
        <div class="z-col z-col-right">
          <h3>加入粉丝团</h3>
          <p>成为黄子弘凡官方粉丝团成员，获取最新资讯和独家福利！</p>
          <ul class="fan-benefits">
            <li>• 演唱会优先购票权</li>
            <li>• 独家周边礼包</li>
            <li>• 线下见面会机会</li>
            <li>• 生日特别礼物</li>
          </ul>
          <button class="btn btn-primary" @click="navigateToPage('contact')">立即加入</button>
        </div>
      </section>
      
      <!-- 第六行：社交媒体关注 -->
      <section class="z-row z-row-6">
        <h2 class="section-title">关注我</h2>
        <div class="social-media-container">
          <div class="social-card">
            <div class="social-icon">🎵</div>
            <h3>微博</h3>
            <p>@黄子弘凡_Lars</p>
            <button class="btn btn-outline">关注</button>
          </div>
          <div class="social-card">
            <div class="social-icon">🎵</div>
            <h3>抖音</h3>
            <p>@黄子弘凡</p>
            <button class="btn btn-outline">关注</button>
          </div>
          <div class="social-card">
            <div class="social-icon">🎵</div>
            <h3>微信公众号</h3>
            <p>黄子弘凡Larsss</p>
            <button class="btn btn-outline">扫码关注</button>
          </div>
          <div class="social-card">
            <div class="social-icon">🎵</div>
            <h3>Bilibili</h3>
            <p>黄子弘凡-Lars</p>
            <button class="btn btn-outline">关注</button>
          </div>
        </div>
      </section>
      
      <!-- 第七行：音乐平台 -->
      <section class="z-row z-row-7">
        <h2 class="section-title">音乐平台</h2>
        <div class="music-platforms">
          <button class="platform-btn">QQ音乐</button>
          <button class="platform-btn">网易云音乐</button>
          <button class="platform-btn">酷狗音乐</button>
          <button class="platform-btn">酷我音乐</button>
          <button class="platform-btn">Spotify</button>
          <button class="platform-btn">Apple Music</button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* 特性容器样式 */
.feature-container {
  width: 100%;
  background-color: #000;
  color: #fff;
}

/* z型布局容器 */
.z-layout-container {
  width: 100%;
  padding: 60px 20px;
}

.z-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 80px;
  align-items: center;
}

.z-row-1, .z-row-5 {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.z-row h2, .z-row h3 {
  margin-bottom: 20px;
  font-weight: 800;
}

.z-row h2 {
  font-size: 2.5rem;
  background: linear-gradient(45deg, #ffffff, #ffb900);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.z-row h3 {
  font-size: 1.8rem;
  color: #ffb900;
}

/* 区域标题 */
.section-title {
  text-align: center;
  margin-bottom: 40px;
  font-size: 2.5rem;
  background: linear-gradient(45deg, #ffffff, #ffb900);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

/* 数据卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(255, 185, 0, 0.2);
}

.stat-value {
  font-size: 2rem;
  font-weight: 900;
  color: #ffb900;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 1rem;
  opacity: 0.8;
}

/* 热门歌曲 */
.z-row-2, .z-row-3, .z-row-4 {
  grid-template-columns: 1fr;
}

.songs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.song-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.song-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(255, 185, 0, 0.2);
}

.song-cover {
  width: 100%;
  height: 250px;
  object-fit: cover;
}

.song-title {
  padding: 15px;
  font-size: 1.1rem;
  text-align: center;
}

/* 最新动态 */
.news-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.news-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.news-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(255, 185, 0, 0.2);
}

.news-image {
  height: 200px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.news-item:hover .news-image img {
  transform: scale(1.05);
}

.news-content {
  padding: 20px;
  flex: 1;
}

.news-date {
  color: #ffb900;
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.news-title {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 10px;
}

/* 巡演日程 */
.events-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.event-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.event-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 185, 0, 0.2);
}

.event-date {
  font-size: 1.1rem;
  font-weight: 700;
  color: #ffb900;
  margin-right: 20px;
  min-width: 120px;
}

.event-info {
  flex: 1;
}

.event-city {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.event-venue {
  font-size: 0.9rem;
  opacity: 0.8;
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

.btn-outline {
  background: transparent;
  border: 2px solid #ffb900;
  color: #ffb900;
}

.btn-outline:hover {
  background: #ffb900;
  color: #000;
  transform: translateY(-2px);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 0.9rem;
}

/* 引用样式 */
.quote-author {
  text-align: right;
  margin: 10px 0 20px 0;
  font-style: italic;
  color: #ffb900;
}

/* 粉丝福利列表 */
.fan-benefits {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}

.fan-benefits li {
  margin-bottom: 10px;
  font-size: 1.1rem;
  color: #ffb900;
}

/* 社交媒体卡片 */
.social-media-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.social-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.social-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(255, 185, 0, 0.3);
}

.social-icon {
  font-size: 3rem;
  margin-bottom: 20px;
  color: #ffb900;
}

.social-card h3 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.social-card p {
  opacity: 0.8;
  margin-bottom: 20px;
}

/* 音乐平台按钮 */
.music-platforms {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  margin-bottom: 40px;
}

.platform-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid #ffb900;
  color: #ffb900;
  padding: 15px 30px;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 150px;
}

.platform-btn:hover {
  background: #ffb900;
  color: #000;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(255, 185, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .z-row-1, .z-row-5 {
    flex-direction: column;
    text-align: center;
  }
  
  .z-col {
    padding: 20px 0;
  }
  
  .songs-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .z-row {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .songs-grid, .news-container, .events-container {
    grid-template-columns: 1fr;
  }
  
  .event-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .event-date {
    margin-right: 0;
  }
  
  .z-layout-container {
    padding: 40px 15px;
  }
  
  .news-item {
    flex-direction: column;
    text-align: center;
  }
  
  .news-image {
    width: 100%;
  }
  
  .section-title {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .songs-grid {
    grid-template-columns: 1fr;
  }
  
  .section-title {
    font-size: 1.8rem;
  }
}
</style>