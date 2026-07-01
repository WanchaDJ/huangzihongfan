import request from '../utils/request'

export function createOrder(data) {
  if (data?.eventId !== undefined) {
    return request({
      url: '/order/create/event',
      method: 'post',
      data
    })
  }

  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export function listMyOrders(data) {
  return request({
    url: '/order/my/list/page',
    method: 'post',
    data
  })
}

export function payOrder(orderId) {
  return request({
    url: `/order/pay/${orderId}`,
    method: 'post'
  })
}

export function cancelOrder(orderId) {
  return request({
    url: `/order/cancel/${orderId}`,
    method: 'post'
  })
}

export function confirmOrder(orderId) {
  return request({
    url: `/order/confirm/${orderId}`,
    method: 'post'
  })
}
