import request from '../utils/request'

export function addComment(data) {
  return request({
    url: '/post/comment/add',
    method: 'post',
    data
  })
}

export function deleteComment(data) {
  return request({
    url: '/post/comment/delete',
    method: 'post',
    data
  })
}

export function listCommentByPostId(data) {
  return request({
    url: '/post/comment/list/page',
    method: 'post',
    data
  })
}
