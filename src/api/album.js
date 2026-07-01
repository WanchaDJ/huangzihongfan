import request from '../utils/request'

export function listAlbumByPage(data) {
  return request({
    url: '/album/list/page/vo',
    method: 'post',
    data
  })
}

export function getAlbumById(id) {
  return request({
    url: '/album/get/vo',
    method: 'get',
    params: { id }
  })
}

export function listTrackByPage(data) {
  return request({
    url: '/track/list/page/vo',
    method: 'post',
    data
  })
}
