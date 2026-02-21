import request from '@/utils/request'

export function getMenuTree() {
  return request({
    url: '/admin/menu/tree',
    method: 'get'
  })
}
