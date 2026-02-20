import request from '@/utils/request'

// C 端用户列表（占位，待后端接口）
export function getUserList(params) {
  return request({
    url: '/admin/c-user/list',
    method: 'get',
    params
  }).catch(() => ({ data: [], total: 0 }))
}

// 更新用户状态（占位）
export function updateUserStatus(uid, status) {
  return request({
    url: '/admin/c-user/status',
    method: 'put',
    data: { uid, status }
  })
}
