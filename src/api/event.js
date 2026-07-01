import request from '../utils/request'

export function listEventByPage(data) {
  return request({
    url: '/event/list/page/vo',
    method: 'post',
    data
  })
}

export function getEventById(id) {
  return request({
    url: '/event/get/vo',
    method: 'get',
    params: { id }
  })
}
