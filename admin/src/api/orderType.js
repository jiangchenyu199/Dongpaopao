import request from '@/utils/request'

export function getOrderTypeList() {
  return request({
    url: '/order-type/list',
    method: 'get'
  })
}

export function getOrderTypeDetail(orderTypeId) {
  return request({
    url: '/order-type/detail',
    method: 'get',
    params: { orderTypeId }
  })
}

export function saveOrderType(data) {
  return request({
    url: '/order-type/save',
    method: 'post',
    data
  })
}

export function deleteOrderType(orderTypeId) {
  return request({
    url: '/order-type/delete',
    method: 'delete',
    params: { orderTypeId }
  })
}
