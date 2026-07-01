<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { addMyPhoto, listPhotoByPage } from '@/api/photo'
import { uploadFile } from '@/api/file'
import { galleryPhotos } from '@/data/huangzihongfan'

const auth = useAuthStore()
const activeCategory = ref('全部')
const lightboxOpen = ref(false)
const lightboxIndex = ref(0)
const backendPhotos = ref([])
const loading = ref(false)
const message = ref('')
const uploadForm = ref({ title: '', description: '', category: '用户上传' })
const uploadPreview = ref('')
const uploadFileRef = ref(null)
const fileInput = ref(null)

const allPhotos = computed(() => {
  const apiPhotos = backendPhotos.value.map((photo) => ({
    id: `api-${photo.id}`,
    src: photo.imageUrl || photo.thumbnailUrl,
    caption: photo.title || '相册图片',
    description: photo.description || '',
    category: photo.categoryName || '用户上传'
  })).filter((photo) => photo.src)
  return [...apiPhotos, ...galleryPhotos]
})

const categories = computed(() => {
  const names = ['全部', ...Array.from(new Set(allPhotos.value.map((photo) => photo.category)))]
  return names.map((name) => ({
    name,
    count: name === '全部' ? allPhotos.value.length : allPhotos.value.filter((photo) => photo.category === name).length
  }))
})

const filteredPhotos = computed(() => {
  if (activeCategory.value === '全部') return allPhotos.value
  return allPhotos.value.filter((photo) => photo.category === activeCategory.value)
})

watch(lightboxOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : ''
})

async function fetchPhotos() {
  loading.value = true
  try {
    const res = await listPhotoByPage({ current: 1, pageSize: 20 })
    backendPhotos.value = res.data?.records || []
  } catch {
    backendPhotos.value = []
  } finally {
    loading.value = false
  }
}

function chooseUpload() {
  if (!auth.isAuthenticated) {
    message.value = '请先登录后再上传图片。'
    return
  }
  fileInput.value?.click()
}

function handleUploadFile(event) {
  const file = event.target.files?.[0]
  if (!file) return
  uploadFileRef.value = file
  if (uploadPreview.value) URL.revokeObjectURL(uploadPreview.value)
  uploadPreview.value = URL.createObjectURL(file)
  if (!uploadForm.value.title) uploadForm.value.title = file.name.replace(/\.[^.]+$/, '')
}

async function submitUpload() {
  if (!auth.isAuthenticated) {
    message.value = '请先登录后再上传图片。'
    return
  }
  if (!uploadFileRef.value) {
    message.value = '请选择图片文件。'
    return
  }
  loading.value = true
  message.value = ''
  try {
    const uploadRes = await uploadFile(uploadFileRef.value, 'gallery_photo')
    await addMyPhoto({
      title: uploadForm.value.title || '用户上传图片',
      description: uploadForm.value.description,
      imageUrl: uploadRes.data,
      categoryName: uploadForm.value.category || '用户上传',
      subCategory: '用户分享'
    })
    message.value = '图片已上传并保存到相册。'
    uploadFileRef.value = null
    uploadPreview.value = ''
    uploadForm.value = { title: '', description: '', category: '用户上传' }
    await fetchPhotos()
  } catch (error) {
    message.value = error.message || '图片上传失败。'
  } finally {
    loading.value = false
  }
}

function openLightbox(index) {
  lightboxIndex.value = index
  lightboxOpen.value = true
}

function closeLightbox() {
  lightboxOpen.value = false
}

function prevPhoto() {
  lightboxIndex.value = (lightboxIndex.value - 1 + filteredPhotos.value.length) % filteredPhotos.value.length
}

function nextPhoto() {
  lightboxIndex.value = (lightboxIndex.value + 1) % filteredPhotos.value.length
}

onMounted(fetchPhotos)
</script>

<template>
  <div class="gallery-page">
    <section class="page-hero">
      <span class="eyebrow">GALLERY</span>
      <h1>精选相册</h1>
      <p>公开图片素材整理，并支持登录用户上传自己的相册图片。</p>
    </section>

    <main class="container">
      <section class="upload-panel">
        <div>
          <h2>上传图片</h2>
          <p>图片会先上传到后端本地存储，再写入 photo 表，刷新后仍可查看。</p>
        </div>
        <form class="upload-form" @submit.prevent="submitUpload">
          <input ref="fileInput" type="file" accept="image/*" hidden @change="handleUploadFile" />
          <button type="button" class="btn-outline" @click="chooseUpload">选择图片</button>
          <input v-model="uploadForm.title" type="text" placeholder="图片标题" />
          <input v-model="uploadForm.description" type="text" placeholder="描述，可选" />
          <button type="submit" class="btn-primary" :disabled="loading">{{ loading ? '处理中...' : '上传保存' }}</button>
        </form>
        <img v-if="uploadPreview" :src="uploadPreview" alt="上传预览" class="upload-preview" />
      </section>

      <p v-if="message" class="message">{{ message }}</p>

      <div class="gallery-tabs">
        <button v-for="category in categories" :key="category.name" :class="{ active: activeCategory === category.name }" @click="activeCategory = category.name">
          {{ category.name }}
          <span>{{ category.count }}</span>
        </button>
      </div>

      <div v-if="filteredPhotos.length > 0" class="gallery-grid">
        <button v-for="(photo, index) in filteredPhotos" :key="photo.id || photo.caption" class="gallery-item" @click="openLightbox(index)">
          <img :src="photo.src" :alt="photo.caption" loading="lazy" decoding="async" />
          <span>{{ photo.caption }}</span>
        </button>
      </div>
      <p v-else class="empty">暂无图片。</p>
    </main>

    <div v-if="lightboxOpen" class="lightbox" @click="closeLightbox">
      <button class="lightbox-close" aria-label="关闭" @click.stop="closeLightbox">×</button>
      <button class="lightbox-prev" aria-label="上一张" @click.stop="prevPhoto">‹</button>
      <figure @click.stop>
        <img :src="filteredPhotos[lightboxIndex]?.src" :alt="filteredPhotos[lightboxIndex]?.caption" />
        <figcaption>{{ filteredPhotos[lightboxIndex]?.caption }}</figcaption>
      </figure>
      <button class="lightbox-next" aria-label="下一张" @click.stop="nextPhoto">›</button>
    </div>
  </div>
</template>

<style scoped>
.gallery-page {
  min-height: 100vh;
  background: var(--color-background);
}

.page-hero {
  padding: 56px 24px;
  text-align: center;
  color: #fff;
  background: linear-gradient(135deg, #1a1815 0%, #2d2418 50%, #19152c 100%);
}

.eyebrow {
  color: var(--color-primary);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.page-hero h1 {
  margin: 8px 0;
  color: #f6d56f;
  font-size: 2.35rem;
}

.page-hero p {
  color: rgba(255, 255, 255, 0.72);
}

.container {
  padding-top: 30px;
  padding-bottom: 60px;
}

.upload-panel {
  display: grid;
  grid-template-columns: 1fr 1.5fr 140px;
  gap: 16px;
  align-items: center;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.upload-panel h2 {
  color: var(--color-heading);
  font-size: 1.2rem;
}

.upload-panel p {
  color: var(--color-text-secondary);
  line-height: 1.65;
}

.upload-form {
  display: grid;
  grid-template-columns: auto 1fr 1fr auto;
  gap: 10px;
}

.upload-form input {
  min-width: 0;
}

.upload-preview {
  width: 140px;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.message {
  padding: 12px 14px;
  margin-bottom: 18px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.gallery-tabs {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin-bottom: 28px;
}

.gallery-tabs button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  color: var(--color-heading);
  cursor: pointer;
}

.gallery-tabs button.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.gallery-tabs span {
  color: inherit;
  opacity: 0.7;
  font-size: 0.78rem;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.gallery-item {
  position: relative;
  overflow: hidden;
  padding: 0;
  border: 0;
  border-radius: var(--radius-md);
  aspect-ratio: 4 / 3;
  background: #eee;
  cursor: pointer;
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.gallery-item:hover img {
  transform: scale(1.06);
}

.gallery-item span {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 24px 14px 12px;
  color: #fff;
  text-align: left;
  font-weight: 700;
  font-size: 0.88rem;
  background: linear-gradient(0deg, rgba(0, 0, 0, 0.7), transparent);
}

.empty {
  padding: 70px 0;
  color: var(--color-text-secondary);
  text-align: center;
}

.lightbox {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.92);
}

.lightbox figure {
  width: min(92vw, 1000px);
  margin: 0;
  text-align: center;
}

.lightbox img {
  max-width: 100%;
  max-height: 82vh;
  object-fit: contain;
  border-radius: var(--radius-sm);
}

.lightbox figcaption {
  margin-top: 12px;
  color: rgba(255, 255, 255, 0.78);
}

.lightbox-close,
.lightbox-prev,
.lightbox-next {
  position: absolute;
  border: 0;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  cursor: pointer;
}

.lightbox-close {
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 1.6rem;
}

.lightbox-prev,
.lightbox-next {
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 68px;
  border-radius: var(--radius-md);
  font-size: 2.6rem;
}

.lightbox-prev {
  left: 18px;
}

.lightbox-next {
  right: 18px;
}

@media (max-width: 960px) {
  .upload-panel,
  .upload-form {
    grid-template-columns: 1fr;
  }

  .gallery-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 640px) {
  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .lightbox-prev,
  .lightbox-next {
    top: auto;
    bottom: 24px;
  }
}
</style>
