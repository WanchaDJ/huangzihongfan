<template>
  <div class="ai-chat-view">
    <ChatInterface />
  </div>
</template>

<script setup>
import ChatInterface from '../components/ChatInterface.vue'
</script>

<style scoped>
.ai-chat-view {
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #ffdf7e 0%, #ffb347 15%, #ff8c66 30%, #ff6b6b 50%, #c44569 70%, #ff85c0 85%, #ffb347 100%);
  background-size: 400% 400%;
  animation: gradientShift 8s ease infinite;
  padding: 15px;
  box-sizing: border-box;
  position: relative;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  25% { background-position: 50% 100%; }
  50% { background-position: 100% 50%; }
  75% { background-position: 50% 0%; }
  100% { background-position: 0% 50%; }
}

/* 动态光斑背景 */
.ai-chat-view::before {
  content: '';
  position: fixed;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: 
    radial-gradient(circle at 20% 30%, rgba(255, 185, 0, 0.3) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(255, 107, 107, 0.3) 0%, transparent 40%),
    radial-gradient(circle at 40% 80%, rgba(196, 69, 105, 0.25) 0%, transparent 40%),
    radial-gradient(circle at 70% 20%, rgba(255, 133, 192, 0.25) 0%, transparent 40%);
  animation: lightOrbit 20s ease-in-out infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes lightOrbit {
  0%, 100% { transform: rotate(0deg) scale(1); }
  33% { transform: rotate(120deg) scale(1.1); }
  66% { transform: rotate(240deg) scale(0.95); }
}

/* 浮动粒子 */
.ai-chat-view::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle, rgba(255, 255, 255, 0.15) 2px, transparent 2px),
    radial-gradient(circle, rgba(255, 185, 0, 0.1) 1px, transparent 1px);
  background-size: 100px 100px, 150px 150px;
  background-position: 0 0, 50px 50px;
  animation: particleFloat 15s linear infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes particleFloat {
  0% { transform: translateY(0) translateX(0); }
  100% { transform: translateY(-100px) translateX(100px); }
}
</style>