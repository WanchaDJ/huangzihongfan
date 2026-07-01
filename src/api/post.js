import request from '../utils/request'

export function addPost(data) {
  return request({ url: '/post/add', method: 'post', data })
}

export function deletePost(data) {
  return request({ url: '/post/delete', method: 'post', data })
}

export function getPostVOById(id) {
  return request({ url: '/post/get/vo', method: 'get', params: { id } })
}

export function listPostVOByPage(data) {
  return request({ url: '/post/list/page/vo', method: 'post', data })
}

export function listMyPostVOByPage(data) {
  return request({ url: '/post/my/list/page/vo', method: 'post', data })
}

export function listMyFavourPostByPage(data) {
  return request({ url: '/post_favour/my/list/page', method: 'post', data })
}

export function doPostThumb(data) {
  return request({ url: '/post_thumb/', method: 'post', data })
}

export function doPostFavour(data) {
  return request({ url: '/post_favour/', method: 'post', data })
}
