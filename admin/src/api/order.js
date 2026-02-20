import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(oid) {
  return request({
    url: `/admin/order/detail/${oid}`,
    method: 'get'
  })
}
