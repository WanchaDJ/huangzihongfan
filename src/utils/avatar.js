export function initialsAvatar(name = '用户') {
  const text = String(name || '用户').trim().slice(0, 2) || '用户'
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 120 120">
      <defs>
        <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
          <stop offset="0" stop-color="#C9A84C"/>
          <stop offset="1" stop-color="#7C3AED"/>
        </linearGradient>
      </defs>
      <rect width="120" height="120" rx="60" fill="url(#g)"/>
      <text x="60" y="68" text-anchor="middle" font-size="34" font-family="Arial, sans-serif" font-weight="700" fill="#fff">${escapeXml(text)}</text>
    </svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

export function resolveAvatar(userOrUrl, fallbackName = '用户') {
  if (typeof userOrUrl === 'string' && isTrustedAvatarUrl(userOrUrl)) return userOrUrl
  const user = userOrUrl || {}
  const url = user.avatar || user.userAvatar
  if (isTrustedAvatarUrl(url)) return url
  return initialsAvatar(user.username || user.userName || fallbackName)
}

function isTrustedAvatarUrl(url) {
  if (!url || typeof url !== 'string') return false
  const value = url.trim()
  if (!value || value.startsWith('/src/')) return false

  if (
    value.startsWith('data:') ||
    value.startsWith('blob:') ||
    value.startsWith('/assets/') ||
    value.startsWith('/api/uploads/') ||
    value.startsWith('/uploads/')
  ) {
    return true
  }

  try {
    const base =
      typeof window !== 'undefined' && window.location?.origin
        ? window.location.origin
        : 'http://localhost'
    const parsed = new URL(value, base)
    if (typeof window !== 'undefined' && parsed.origin === window.location.origin) {
      return true
    }
    return parsed.hostname === 'localhost' || parsed.hostname === '127.0.0.1' || value.includes('/api/uploads/')
  } catch {
    return false
  }
}

function escapeXml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
}
