import request from '../utils/request'

export function uploadFile(file, biz = 'gallery_photo') {
  const data = new FormData()
  data.append('file', file)
  data.append('biz', biz)
  return request({
    url: '/file/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
