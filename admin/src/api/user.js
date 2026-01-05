import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/admin/user/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/admin/user/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/admin/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/admin/user/info',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/admin/user/password',
    method: 'put',
    data
  })
}

export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

export function addUser(data) {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/user/update',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

export function batchDeleteUsers(ids) {
  return request({
    url: '/admin/user/batch',
    method: 'delete',
    data: { ids }
  })
}

export function toggleUserStatus(id, status) {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function batchToggleUserStatus(ids, status) {
  return request({
    url: '/admin/user/batch/status',
    method: 'put',
    data: { ids, status }
  })
}
