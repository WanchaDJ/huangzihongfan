import request from '../utils/request'

export function sendContactMessage(data) {
  return request({
    url: '/contact/send',
    method: 'post',
    data
  })
}
