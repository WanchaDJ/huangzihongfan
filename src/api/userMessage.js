import request from '../utils/request'

export function sendUserMessage(data) {
  return request({ url: '/message/send', method: 'post', data })
}

export function listConversations() {
  return request({ url: '/message/conversation/list', method: 'get' })
}

export function getMessageThread(otherUserId) {
  return request({ url: '/message/thread', method: 'get', params: { otherUserId } })
}

export function readConversation(otherUserId) {
  return request({ url: '/message/read/conversation', method: 'post', data: { otherUserId } })
}

export function getUnreadMessageCount() {
  return request({ url: '/message/unread/count', method: 'get' })
}
