import request from '../utils/request'

export function createConversation(data) {
  return request({
    url: '/chat/conversation/create',
    method: 'post',
    data
  })
}

export function listMyConversations() {
  return request({
    url: '/chat/conversation/my/list',
    method: 'get'
  })
}

export function deleteConversation(data) {
  return request({
    url: '/chat/conversation/delete',
    method: 'post',
    data
  })
}

export function sendMessage(data) {
  return request({
    url: '/chat/message/send',
    method: 'post',
    data
  })
}

export function listMessages(data) {
  return request({
    url: '/chat/message/list',
    method: 'post',
    data
  })
}
