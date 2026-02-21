import request from '@/utils/request'

export function getRoleList() {
  return request({
    url: '/admin/role/list',
    method: 'get'
  })
}

export function getRoleDetail(id) {
  return request({
    url: `/admin/role/detail/${id}`,
    method: 'get'
  })
}

export function saveRole(data) {
  return request({
    url: '/admin/role/save',
    method: 'post',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/admin/role/delete/${id}`,
    method: 'delete'
  })
}
