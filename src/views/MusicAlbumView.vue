<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { listAlbumByPage, listTrackByPage } from '@/api/album'
import { albums as verifiedAlbums, officialLinks, openExternal, tracks as verifiedTracks } from '@/data/huangzihongfan'

const route = useRoute()

const activeTab = ref('全部')
const tabs = ['全部', 'EP', '专辑', '单曲']
const loading = ref(false)
const errorMsg = ref('')
const albums = ref([])
const tracks = ref([])
const selectedAlbumId = ref(null)
const currentTrackId = ref(null)
const isPlaying = ref(false)
const audioRef = ref(null)

function musicSearchUrl(keyword) {
  return `https://y.qq.com/n/ryqq/search?w=${encodeURIComponent(`黄子弘凡 ${keyword}`)}`
}

function sortTracks(list) {
  return [...list].sort((a, b) => {
    const aIndex = Number(a.trackNumber || 999)
    const bIndex = Number(b.trackNumber || 999)
    if (aIndex !== bIndex) return aIndex - bIndex
    return String(a.title || '').localeCompare(String(b.title || ''), 'zh-CN')
  })
}

function normalizeTrack(item) {
  const title = item.title || '未命名曲目'
  return {
    id: item.id,
    title,
    album: item.albumTitle || item.album || '单曲',
    albumId: item.albumId ?? null,
    year: item.year || item.releaseYear || '',
    cover: item.coverImage || item.cover || '',
    duration: item.durationText || item.duration || '',
    durationMs: Number(item.durationMs || 0),
    previewUrl: item.previewUrl || item.audioUrl || '',
    sourceUrl: item.sourceUrl || item.audioUrl || musicSearchUrl(title),
    trackNumber: item.trackNumber || null,
    description: item.description || ''
  }
}

function normalizeAlbum(item) {
  return {
    id: item.id,
    title: item.title,
    artist: item.artist || '黄子弘凡',
    year: item.releaseYear || item.year || '',
    type: item.type || 'EP',
    cover: item.coverImage || item.cover || '',
    description: item.description || '',
    sourceUrl: item.sourceUrl || musicSearchUrl(item.title || '黄子弘凡'),
    tracks: Array.isArray(item.tracks)
      ? item.tracks.map((track) =>
          typeof track === 'string'
            ? normalizeTrack({ title: track, album: item.title, albumId: item.id })
            : normalizeTrack(track)
        )
      : []
  }
}

function trackKey(track) {
  return `${track.albumId ?? 'single'}::${track.title}`
}

function mergeTrack(primary, incoming) {
  return normalizeTrack({
    id: primary.id ?? incoming?.id,
    title: primary.title || incoming?.title,
    album: primary.album || incoming?.album,
    albumTitle: primary.album || incoming?.albumTitle,
    albumId: primary.albumId ?? incoming?.albumId ?? null,
    year: primary.year || incoming?.year || incoming?.releaseYear || '',
    cover: primary.cover || incoming?.coverImage || incoming?.cover || '',
    duration: primary.duration || incoming?.durationText || incoming?.duration || '',
    durationMs: primary.durationMs || incoming?.durationMs || 0,
    previewUrl: primary.previewUrl || incoming?.previewUrl || incoming?.audioUrl || '',
    sourceUrl: primary.sourceUrl || incoming?.sourceUrl || incoming?.audioUrl || '',
    trackNumber: primary.trackNumber || incoming?.trackNumber || null,
    description: primary.description || incoming?.description || ''
  })
}

function mergeAlbum(primary, incoming) {
  return normalizeAlbum({
    id: primary.id ?? incoming?.id,
    title: primary.title || incoming?.title,
    artist: primary.artist || incoming?.artist || '黄子弘凡',
    year: primary.year || incoming?.releaseYear || incoming?.year || '',
    type: primary.type || incoming?.type || 'EP',
    cover: primary.cover || incoming?.coverImage || incoming?.cover || '',
    description: primary.description || incoming?.description || '',
    sourceUrl: primary.sourceUrl || incoming?.sourceUrl || '',
    tracks: primary.tracks?.length ? primary.tracks : incoming?.tracks || []
  })
}

function hydrateAlbums(albumList, trackList) {
  return albumList.map((album) => {
    const mergedTrackMap = new Map()
    ;[...(album.tracks || []), ...trackList.filter((track) => track.albumId === album.id)].forEach((track) => {
      const normalized = normalizeTrack({
        ...track,
        album: track.album || album.title,
        albumId: track.albumId ?? album.id
      })
      mergedTrackMap.set(trackKey(normalized), normalized)
    })
    return {
      ...album,
      tracks: sortTracks([...mergedTrackMap.values()])
    }
  })
}

function buildLocalTracks() {
  const merged = new Map()
  verifiedAlbums.forEach((album) => {
    ;(album.tracks || []).forEach((track) => {
      const normalized = normalizeTrack(track)
      merged.set(trackKey(normalized), normalized)
    })
  })
  verifiedTracks.forEach((track) => {
    const normalized = normalizeTrack(track)
    if (!merged.has(trackKey(normalized))) {
      merged.set(trackKey(normalized), normalized)
    }
  })
  return [...merged.values()]
}

function seedLocalData() {
  const localTracks = buildLocalTracks()
  const localAlbums = hydrateAlbums(verifiedAlbums.map(normalizeAlbum), localTracks)
  tracks.value = localTracks
  albums.value = localAlbums
  if (!selectedAlbumId.value && localAlbums.length) {
    selectedAlbumId.value = localAlbums[0].id
  }
}

const filteredAlbums = computed(() => {
  if (activeTab.value === '单曲') return []
  if (activeTab.value === '全部') return albums.value
  return albums.value.filter((item) => item.type === activeTab.value)
})

const singleTracks = computed(() =>
  [...tracks.value]
    .filter((track) => !track.albumId)
    .sort((a, b) => Number(b.year || 0) - Number(a.year || 0) || String(a.title).localeCompare(String(b.title), 'zh-CN'))
)

const selectedAlbum = computed(() => {
  if (activeTab.value === '单曲') return null
  return (
    filteredAlbums.value.find((item) => item.id === selectedAlbumId.value) ||
    albums.value.find((item) => item.id === selectedAlbumId.value) ||
    filteredAlbums.value[0] ||
    null
  )
})

const albumTracks = computed(() => sortTracks(selectedAlbum.value?.tracks || []))

const visibleTracks = computed(() => (activeTab.value === '单曲' ? singleTracks.value : albumTracks.value))

const currentTrack = computed(() => tracks.value.find((track) => track.id === currentTrackId.value) || null)

const musicMetrics = computed(() => [
  { label: '作品总数', value: tracks.value.length },
  { label: '专辑 / EP', value: albums.value.length },
  { label: '公开单曲', value: singleTracks.value.length }
])

function ensureSelectedAlbum() {
  if (activeTab.value === '单曲') return
  if (!filteredAlbums.value.length) {
    selectedAlbumId.value = null
    return
  }
  if (!filteredAlbums.value.some((album) => album.id === selectedAlbumId.value)) {
    selectedAlbumId.value = filteredAlbums.value[0].id
  }
}

async function fetchMusicData() {
  seedLocalData()
  loading.value = true
  errorMsg.value = ''

  const localTracks = buildLocalTracks()
  const localAlbums = verifiedAlbums.map(normalizeAlbum)

  const [albumRes, trackRes] = await Promise.allSettled([
    listAlbumByPage({ current: 1, pageSize: 20 }),
    listTrackByPage({ current: 1, pageSize: 50 })
  ])

  if (albumRes.status === 'rejected' || trackRes.status === 'rejected') {
    errorMsg.value = '后端音乐库未返回完整数据，当前页面优先展示已核验的公开资料。'
  }

  const apiTracks = trackRes.status === 'fulfilled' ? (trackRes.value.data?.records || []).map(normalizeTrack) : []
  const apiAlbums = albumRes.status === 'fulfilled' ? (albumRes.value.data?.records || []).map(normalizeAlbum) : []

  const apiTrackById = new Map(apiTracks.map((track) => [String(track.id), track]))
  const apiTrackByTitle = new Map(apiTracks.map((track) => [track.title, track]))
  const apiAlbumById = new Map(apiAlbums.map((album) => [String(album.id), album]))
  const apiAlbumByTitle = new Map(apiAlbums.map((album) => [album.title, album]))

  const mergedTracks = localTracks.map((track) => {
    const match = apiTrackById.get(String(track.id)) || apiTrackByTitle.get(track.title)
    return match ? mergeTrack(track, match) : track
  })

  const mergedAlbums = localAlbums.map((album) => {
    const match = apiAlbumById.get(String(album.id)) || apiAlbumByTitle.get(album.title)
    return match ? mergeAlbum(album, match) : album
  })

  tracks.value = mergedTracks
  albums.value = hydrateAlbums(mergedAlbums, mergedTracks)
  loading.value = false
  ensureSelectedAlbum()
  syncRouteSelection()
}

function selectAlbum(albumId) {
  if (selectedAlbumId.value === albumId) return
  selectedAlbumId.value = albumId
  currentTrackId.value = null
  stopAudio()
}

function stopAudio() {
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value.currentTime = 0
  }
  isPlaying.value = false
}

function playTrack(track) {
  if (!track) return
  if (!track.previewUrl) {
    openExternal(track.sourceUrl || musicSearchUrl(track.title))
    return
  }

  if (currentTrackId.value === track.id && audioRef.value) {
    if (isPlaying.value) {
      audioRef.value.pause()
      isPlaying.value = false
    } else {
      audioRef.value.play()
      isPlaying.value = true
    }
    return
  }

  currentTrackId.value = track.id
}

function playSelectedAlbum() {
  const target = albumTracks.value.find((track) => track.previewUrl) || albumTracks.value[0]
  if (target) playTrack(target)
}

function openTrackSource(track) {
  openExternal(track?.sourceUrl || musicSearchUrl(track?.title || '黄子弘凡'))
}

function syncRouteSelection() {
  const queryTrackId = Number(route.query.track)
  if (!queryTrackId) {
    ensureSelectedAlbum()
    return
  }

  const targetTrack = tracks.value.find((item) => Number(item.id) === queryTrackId)
  if (!targetTrack) return

  currentTrackId.value = targetTrack.id
  if (targetTrack.albumId) {
    selectedAlbumId.value = targetTrack.albumId
    activeTab.value = '全部'
  } else {
    activeTab.value = '单曲'
  }
}

watch(currentTrackId, async (trackId) => {
  const track = tracks.value.find((item) => item.id === trackId)
  if (!track?.previewUrl) return

  await Promise.resolve()
  if (!audioRef.value) return

  audioRef.value.src = track.previewUrl
  try {
    await audioRef.value.play()
    isPlaying.value = true
  } catch {
    isPlaying.value = false
  }
})

watch([activeTab, filteredAlbums], () => {
  ensureSelectedAlbum()
})

watch(
  () => route.query.track,
  () => {
    syncRouteSelection()
  }
)

onMounted(() => {
  fetchMusicData()
})

onBeforeUnmount(() => {
  stopAudio()
})
</script>

<template>
  <div class="music-page">
    <section class="page-hero">
      <span class="eyebrow">MUSIC</span>
      <h1>音乐作品</h1>
      <p>按专辑、EP 与公开单曲整理，优先展示已核验的真实封面与曲目信息，点击专辑即可查看对应歌曲。</p>
      <div class="hero-metrics">
        <div v-for="item in musicMetrics" :key="item.label" class="metric-item">
          <strong>{{ item.value }}</strong>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </section>

    <main class="container">
      <div class="toolbar">
        <div class="tabs">
          <button v-for="tab in tabs" :key="tab" :class="{ active: activeTab === tab }" @click="activeTab = tab">
            {{ tab }}
          </button>
        </div>
        <div class="toolbar-actions">
          <button class="platform-btn" @click="openExternal(officialLinks.appleMusic)">Apple Music 艺人页</button>
          <button class="btn-outline" @click="openExternal(officialLinks.qqMusicSearch)">QQ 音乐搜索</button>
        </div>
      </div>

      <p v-if="errorMsg" class="notice">{{ errorMsg }}</p>
      <p v-if="loading" class="notice">正在同步后端音乐库数据...</p>

      <section class="player-panel">
        <div class="player-info">
          <img :src="currentTrack?.cover || selectedAlbum?.cover || albums[0]?.cover" :alt="currentTrack?.title || selectedAlbum?.title || '黄子弘凡音乐作品'" />
          <div>
            <span class="player-mark">{{ currentTrack ? '当前试听' : activeTab === '单曲' ? '公开单曲' : '当前专辑' }}</span>
            <h2>{{ currentTrack?.title || selectedAlbum?.title || '黄子弘凡音乐作品' }}</h2>
            <p v-if="currentTrack">
              {{ currentTrack.album }}<span v-if="currentTrack.duration"> · {{ currentTrack.duration }}</span>
            </p>
            <p v-else-if="selectedAlbum">
              {{ selectedAlbum.artist }} · {{ selectedAlbum.year }} · {{ selectedAlbum.type }}
            </p>
            <p v-else>点击曲目即可站内试听，未提供试听的作品会跳转到公开平台原页。</p>
          </div>
        </div>
        <div class="player-actions">
          <button
            class="platform-btn"
            :disabled="!currentTrack && activeTab !== '单曲' && !selectedAlbum"
            @click="currentTrack ? playTrack(currentTrack) : activeTab === '单曲' ? playTrack(singleTracks[0]) : playSelectedAlbum()"
          >
            {{
              currentTrack
                ? currentTrack.previewUrl
                  ? isPlaying
                    ? '暂停试听'
                    : '播放试听'
                  : '打开平台'
                : activeTab === '单曲'
                  ? '播放最新单曲'
                  : '播放专辑'
            }}
          </button>
          <button
            class="btn-outline"
            :disabled="!currentTrack && activeTab !== '单曲' && !selectedAlbum"
            @click="currentTrack ? openTrackSource(currentTrack) : openExternal(selectedAlbum?.sourceUrl || officialLinks.appleMusic)"
          >
            打开原页
          </button>
        </div>
        <audio ref="audioRef" @ended="isPlaying = false"></audio>
      </section>

      <section class="music-layout">
        <aside class="catalog-panel">
          <div class="panel-head">
            <div>
              <span class="catalog-mark">{{ activeTab === '单曲' ? 'SINGLES' : 'ALBUM LIBRARY' }}</span>
              <h2>{{ activeTab === '单曲' ? '公开单曲' : '专辑目录' }}</h2>
            </div>
            <strong>{{ activeTab === '单曲' ? singleTracks.length : filteredAlbums.length }}</strong>
          </div>

          <div v-if="activeTab === '单曲'" class="catalog-list">
            <button
              v-for="track in singleTracks"
              :key="track.id"
              class="catalog-item"
              :class="{ active: currentTrackId === track.id }"
              @click="playTrack(track)"
            >
              <img :src="track.cover" :alt="track.title" />
              <div>
                <h3>{{ track.title }}</h3>
                <p>{{ track.year }} · {{ track.album }}</p>
              </div>
            </button>
          </div>

          <div v-else class="catalog-list">
            <button
              v-for="album in filteredAlbums"
              :key="album.id"
              class="catalog-item"
              :class="{ active: selectedAlbumId === album.id }"
              @click="selectAlbum(album.id)"
            >
              <img :src="album.cover" :alt="album.title" />
              <div>
                <h3>{{ album.title }}</h3>
                <p>{{ album.year }} · {{ album.type }}</p>
              </div>
              <span>{{ album.tracks.length }} 首</span>
            </button>
          </div>
        </aside>

        <section class="detail-panel">
          <template v-if="activeTab === '单曲'">
            <div class="album-hero">
              <div class="album-copy single-copy">
                <span class="album-badge">SINGLE</span>
                <h2>公开单曲列表</h2>
                <p class="album-meta">按照年份与作品名整理，支持试听预览或跳转到公开平台原页。</p>
              </div>
            </div>
          </template>

          <template v-else-if="selectedAlbum">
            <div class="album-hero">
              <img :src="selectedAlbum.cover" :alt="selectedAlbum.title" />
              <div class="album-copy">
                <span class="album-badge">{{ selectedAlbum.type }}</span>
                <h2>{{ selectedAlbum.title }}</h2>
                <p class="album-meta">
                  {{ selectedAlbum.artist }} · {{ selectedAlbum.year }} · {{ selectedAlbum.tracks.length }} 首作品
                </p>
                <p class="album-description">{{ selectedAlbum.description }}</p>
                <div class="album-actions">
                  <button class="platform-btn" @click="openExternal(selectedAlbum.sourceUrl)">打开专辑</button>
                  <button class="btn-outline" @click="playSelectedAlbum">按专辑试听</button>
                </div>
              </div>
            </div>
          </template>

          <div class="section-head">
            <h3>{{ activeTab === '单曲' ? '单曲列表' : `${selectedAlbum?.title || '专辑'} 曲目` }}</h3>
            <p>点击“试听”可播放公开预览片段，点击“原页”可打开对应平台页面。</p>
          </div>

          <div class="track-list">
            <article v-for="(track, index) in visibleTracks" :key="track.id" class="track-item" :class="{ active: currentTrackId === track.id }">
              <span class="rank">{{ String(track.trackNumber || index + 1).padStart(2, '0') }}</span>
              <img :src="track.cover" :alt="track.title" />
              <div class="track-info">
                <h3>{{ track.title }}</h3>
                <p>{{ track.album }}<span v-if="track.year"> · {{ track.year }}</span></p>
              </div>
              <span class="duration">{{ track.duration || '平台收听' }}</span>
              <div class="track-actions">
                <button class="platform-btn" @click="playTrack(track)">
                  {{ currentTrackId === track.id && isPlaying ? '暂停' : track.previewUrl ? '试听' : '收听' }}
                </button>
                <button class="btn-outline" @click="openTrackSource(track)">原页</button>
              </div>
            </article>
            <p v-if="visibleTracks.length === 0" class="empty">当前分类暂无可展示的作品。</p>
          </div>
        </section>
      </section>
    </main>
  </div>
</template>

<style scoped>
.music-page {
  min-height: 100vh;
  background: var(--color-background);
}

.page-hero {
  padding: 56px 24px;
  text-align: center;
  color: #fff;
  background: linear-gradient(135deg, #171411 0%, #2a2117 48%, #191726 100%);
}

.eyebrow,
.catalog-mark,
.player-mark {
  color: var(--color-primary);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.page-hero h1 {
  margin: 8px 0 10px;
  color: #f4cf6a;
  font-size: 2.4rem;
}

.page-hero p {
  margin: 0 auto;
  max-width: 760px;
  color: rgba(255, 255, 255, 0.78);
}

.hero-metrics {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 22px;
  flex-wrap: wrap;
}

.metric-item {
  min-width: 112px;
  padding: 14px 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.06);
}

.metric-item strong {
  display: block;
  color: #f4cf6a;
  font-size: 1.2rem;
}

.metric-item span {
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.82rem;
}

.container {
  padding-top: 34px;
  padding-bottom: 60px;
}

.toolbar,
.toolbar-actions,
.tabs,
.player-actions,
.album-actions,
.track-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar {
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 22px;
  flex-wrap: wrap;
}

.tabs {
  flex-wrap: wrap;
}

.tabs button,
.platform-btn,
.btn-outline {
  padding: 9px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  color: var(--color-heading);
  cursor: pointer;
}

.tabs button.active,
.platform-btn {
  border-color: var(--color-primary);
  background: var(--color-primary);
  color: #17130e;
  font-weight: 800;
}

.btn-outline:hover,
.tabs button:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.notice {
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
}

.player-panel,
.catalog-panel,
.detail-panel,
.track-item {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: #fff;
}

.player-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 20px;
  margin-bottom: 24px;
}

.player-info {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.player-info img {
  width: 88px;
  height: 88px;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.player-info h2,
.panel-head h2,
.album-copy h2,
.section-head h3,
.catalog-item h3,
.track-info h3 {
  color: var(--color-heading);
}

.player-info h2 {
  margin: 4px 0 6px;
  font-size: 1.22rem;
}

.player-info p,
.panel-head strong,
.catalog-item p,
.album-meta,
.album-description,
.track-info p,
.duration,
.section-head p {
  color: var(--color-text-secondary);
}

.music-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 20px;
}

.catalog-panel,
.detail-panel {
  padding: 18px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.panel-head h2 {
  margin-top: 6px;
  font-size: 1.18rem;
}

.panel-head strong {
  font-size: 1.4rem;
  font-weight: 800;
}

.catalog-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.catalog-item {
  display: grid;
  grid-template-columns: 58px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: #fff;
  text-align: left;
  cursor: pointer;
}

.catalog-item img {
  width: 58px;
  height: 58px;
  border-radius: var(--radius-sm);
  object-fit: cover;
}

.catalog-item.active,
.catalog-item:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-pale);
}

.catalog-item h3 {
  margin-bottom: 4px;
  font-size: 0.95rem;
}

.catalog-item p,
.catalog-item span {
  font-size: 0.8rem;
}

.album-hero {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 20px;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border);
}

.album-hero img {
  width: 100%;
  aspect-ratio: 1;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.album-copy h2 {
  margin: 10px 0 8px;
  font-size: 1.7rem;
}

.album-badge {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--color-primary-pale);
  color: var(--color-primary-dark);
  font-size: 0.8rem;
  font-weight: 800;
}

.album-meta {
  margin-bottom: 10px;
}

.album-description {
  line-height: 1.75;
  margin-bottom: 16px;
}

.single-copy {
  min-height: 140px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.section-head {
  margin-bottom: 16px;
}

.section-head h3 {
  margin-bottom: 6px;
  font-size: 1.18rem;
}

.track-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.track-item {
  display: grid;
  grid-template-columns: 44px 56px minmax(0, 1fr) 88px auto;
  gap: 14px;
  align-items: center;
  padding: 14px;
}

.track-item.active {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

.rank {
  color: var(--color-primary-dark);
  font-weight: 800;
  text-align: center;
}

.track-item img {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-sm);
  object-fit: cover;
}

.track-info h3 {
  margin-bottom: 4px;
  font-size: 0.98rem;
}

.track-info p,
.duration {
  font-size: 0.84rem;
}

.empty {
  padding: 24px 0;
  color: var(--color-text-secondary);
  text-align: center;
}

@media (max-width: 1080px) {
  .music-layout {
    grid-template-columns: 1fr;
  }

  .player-panel {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 760px) {
  .album-hero {
    grid-template-columns: 1fr;
  }

  .track-item {
    grid-template-columns: 40px 50px minmax(0, 1fr);
  }

  .duration {
    display: none;
  }

  .track-actions {
    grid-column: 1 / -1;
    justify-content: flex-end;
  }
}

@media (max-width: 640px) {
  .tabs,
  .toolbar-actions,
  .player-actions,
  .album-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .player-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .catalog-item {
    grid-template-columns: 52px minmax(0, 1fr);
  }

  .catalog-item span {
    display: none;
  }
}
</style>
