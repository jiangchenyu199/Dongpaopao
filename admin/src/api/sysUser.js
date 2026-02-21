import request from '@/utils/request'

export function getSysUserList(params) {
  return request({
    url: '/admin/sys-user/list',
    method: 'get',
    params
  })
}

export function getSysUserDetail(id) {
  return request({
    url: `/admin/sys-user/detail/${id}`,
    method: 'get'
  })
}

export function saveSysUser(data) {
  return request({
    url: '/admin/sys-user/save',
    method: 'post',
    data
  })
}

export function deleteSysUser(id) {
  return request({
    url: `/admin/sys-user/delete/${id}`,
    method: 'delete'
  })
}
