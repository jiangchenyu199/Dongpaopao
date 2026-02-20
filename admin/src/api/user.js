import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/admin/c-user/list',
    method: 'get',
    params
  }).catch(() => ({ data: [], total: 0 }))
}

export function updateUserStatus(uid, status) {
  return request({
    url: '/admin/c-user/status',
    method: 'put',
    data: { uid, status }
  })
}
