import request from '../utils/request'

export function listMemberBenefits() {
  return request({
    url: '/member/benefit/list',
    method: 'post'
  })
}

export function getMyGrowthRecords() {
  return request({
    url: '/member/growth/my/list',
    method: 'get'
  })
}

export function getMyConsumptionRecords(data) {
  return request({
    url: '/member/consumption/my/list/page',
    method: 'post',
    data
  })
}

export function getMyPointsRecords(data) {
  return request({
    url: '/member/points/my/list/page',
    method: 'post',
    data
  })
}

export function getMyPointsTotal() {
  return request({
    url: '/member/points/my/total',
    method: 'get'
  })
}

export function getUserProfile() {
  return request({
    url: '/user/profile/get',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/user/profile/update',
    method: 'post',
    data
  })
}
