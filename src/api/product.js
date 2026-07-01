import request from '../utils/request'

export function listProductByPage(data) {
  return request({
    url: '/product/list/page/vo',
    method: 'post',
    data
  })
}

export function getProductById(id) {
  return request({
    url: '/product/get/vo',
    method: 'get',
    params: { id }
  })
}
