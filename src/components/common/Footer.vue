<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 导航函数
function navigateToPage(page) {
  router.push(`/${page}`)
}

// 当前年份
const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="footer-section">
    <div class="footer-container">
      <!-- 左侧：关于信息 -->
      <div class="footer-column">
        <h3>关于黄子弘凡</h3>
        <p>中国内地新生代实力唱将，拥有独特的嗓音和出色的舞台表现力。</p>
        <p>自出道以来，凭借其精湛的唱功和迷人的舞台魅力，迅速获得了众多粉丝的喜爱和认可。</p>
      </div>
      
      <!-- 中间：快速链接 -->
      <div class="footer-column">
        <h3>快速链接</h3>
        <ul class="footer-links">
          <li><button class="footer-link-btn" @click="navigateToPage('')">首页</button></li>
          <li><button class="footer-link-btn" @click="navigateToPage('about')">关于</button></li>
          <li><button class="footer-link-btn" @click="navigateToPage('gallery')">相册</button></li>
          <li><button class="footer-link-btn" @click="navigateToPage('music-album')">音乐</button></li>
          <li><button class="footer-link-btn" @click="navigateToPage('news')">动态</button></li>
          <li><button class="footer-link-btn" @click="navigateToPage('contact')">联系我们</button></li>
        </ul>
      </div>
      
      <!-- 右侧：联系方式 -->
      <div class="footer-column">
        <h3>联系我们</h3>
        <div class="contact-info">
          <p><strong>官方邮箱：</strong>contact@huangzihongfan.com</p>
          <p><strong>商务合作：</strong>business@huangzihongfan.com</p>
          <p><strong>粉丝热线：</strong>+86 123 4567 8910</p>
        </div>
        <div class="social-icons">
          <button class="social-icon-btn">微博</button>
          <button class="social-icon-btn">抖音</button>
          <button class="social-icon-btn">微信</button>
          <button class="social-icon-btn">B站</button>
        </div>
      </div>
    </div>
    
    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} 黄子弘凡官方网站. 版权所有.</p>
      <div class="footer-policy">
        <button class="policy-btn">隐私政策</button>
        <button class="policy-btn">使用条款</button>
        <button class="policy-btn">Cookie政策</button>
      </div>
    </div>
  </footer>
</template>

<style scoped>
/* 页脚样式 */
.footer-section {
  background-color: #1a1a1a;
  color: #fff;
  padding: 40px 20px 20px;
  width: 100%;
}

.footer-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto 40px;
}

.footer-column {
  flex: 1;
  min-width: 250px;
}

.footer-column h3 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #ffb900;
  font-weight: 700;
}

.footer-column p {
  margin-bottom: 15px;
  line-height: 1.6;
  opacity: 0.8;
}

/* 链接样式 */
.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-link-btn {
  background: none;
  border: none;
  color: #fff;
  opacity: 0.8;
  cursor: pointer;
  font-size: 1rem;
  padding: 5px 0;
  transition: all 0.3s ease;
  text-align: left;
}

.footer-link-btn:hover {
  color: #ffb900;
  opacity: 1;
  transform: translateX(5px);
}

/* 联系信息 */
.contact-info {
  margin-bottom: 20px;
}

.contact-info p {
  margin-bottom: 10px;
}

.contact-info strong {
  color: #ffb900;
}

/* 社交图标 */
.social-icons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.social-icon-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 185, 0, 0.3);
  color: #fff;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.social-icon-btn:hover {
  background: #ffb900;
  color: #000;
  transform: translateY(-2px);
}

/* 底部版权信息 */
.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  max-width: 1200px;
  margin: 0 auto;
}

.footer-bottom p {
  margin-bottom: 15px;
  opacity: 0.7;
  font-size: 0.9rem;
}

.footer-policy {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.policy-btn {
  background: none;
  border: none;
  color: #fff;
  opacity: 0.7;
  cursor: pointer;
  font-size: 0.9rem;
  padding: 5px 10px;
  transition: all 0.3s ease;
}

.policy-btn:hover {
  color: #ffb900;
  opacity: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-container {
    flex-direction: column;
    gap: 30px;
  }
  
  .footer-column {
    min-width: 100%;
    text-align: center;
  }
  
  .footer-links li {
    text-align: center;
  }
  
  .footer-link-btn {
    text-align: center;
    width: 100%;
  }
  
  .social-icons {
    justify-content: center;
  }
  
  .footer-policy {
    flex-direction: column;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .footer-section {
    padding: 30px 15px 15px;
  }
  
  .footer-column h3 {
    font-size: 1.3rem;
  }
  
  .footer-bottom p {
    font-size: 0.85rem;
  }
  
  .policy-btn {
    font-size: 0.85rem;
  }
}
</style>