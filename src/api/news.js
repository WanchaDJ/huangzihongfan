import request from '../utils/request'

export function listNewsByPage(data) {
  return request({
    url: '/news/list/page/vo',
    method: 'post',
    data
  })
}

export function getNewsById(id) {
  return request({
    url: '/news/get/vo',
    method: 'get',
    params: { id }
  })
}
