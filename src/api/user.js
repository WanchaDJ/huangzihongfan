import request from '../utils/request'

export function userRegister(data) {
  return request({ url: '/user/register', method: 'post', data })
}

export function userLogin(data) {
  return request({ url: '/user/login', method: 'post', data })
}

export function getLoginUser() {
  return request({ url: '/user/get/login', method: 'get' })
}

export function getUserVOById(id) {
  return request({ url: '/user/get/vo', method: 'get', params: { id } })
}

export function listUserVOByPage(data) {
  return request({ url: '/user/list/page/vo', method: 'post', data })
}

export function updateMyUser(data) {
  return request({ url: '/user/update/my', method: 'post', data })
}

export function updatePassword(data) {
  return request({ url: '/user/update/password', method: 'post', data })
}

export function userLogout() {
  return request({ url: '/user/logout', method: 'post' })
}
