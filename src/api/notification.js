import request from '../utils/request'

export function listMyNotifications() {
  return request({
    url: '/notification/my/list',
    method: 'get'
  })
}

export function markNotificationRead(data) {
  return request({
    url: '/notification/read',
    method: 'post',
    data
  })
}
