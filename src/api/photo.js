import request from '../utils/request'

export function listPhotoCategory() {
  return request({ url: '/photo/category/list', method: 'post' })
}

export function listPhotoByPage(data) {
  return request({ url: '/photo/list/page/vo', method: 'post', data })
}

export function addMyPhoto(data) {
  return request({ url: '/photo/my/add', method: 'post', data })
}
