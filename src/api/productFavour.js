import request from '../utils/request'

export function doProductFavour(data) {
  return request({
    url: '/product_favour/',
    method: 'post',
    data
  })
}

export function listMyFavourProductByPage(data) {
  return request({
    url: '/product_favour/my/list/page',
    method: 'post',
    data
  })
}
