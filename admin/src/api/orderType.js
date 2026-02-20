import request from '@/utils/request'

export function getOrderTypeList() {
  return request({
    url: '/order-type/list',
    method: 'get'
  })
}
