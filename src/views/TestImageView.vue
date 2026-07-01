<template>
  <div class="test-container">
    <h1>图片加载测试</h1>
    <!-- 测试直接导入的图片 -->
    <div class="test-image-section">
      <h2>导入方式</h2>
      <img :src="testImg" alt="测试图片" class="test-img" />
    </div>
    
    <!-- 测试相对路径 -->
    <div class="test-image-section">
      <h2>相对路径</h2>
      <img src="../assets/黄子弘凡/webwxgetmsgimg (8).jpg" alt="相对路径测试" class="test-img" />
    </div>
  </div>
</template>

<script setup>
import testImg from '../assets/黄子弘凡/webwxgetmsgimg (8).jpg'
</script>

<style scoped>
.test-container {
  padding: 40px;
  color: white;
  background-color: #000;
  min-height: 100vh;
}

.test-image-section {
  margin: 20px 0;
}

.test-img {
  max-width: 500px;
  max-height: 300px;
  border: 2px solid white;
}
</style>