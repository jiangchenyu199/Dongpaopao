import request from '@/utils/request'

export function getSystemConfigList() {
  return request({
    url: '/admin/system/config/list',
    method: 'get'
  })
}

export function updateSystemConfig(data) {
  return request({
    url: '/admin/system/config/update',
    method: 'put',
    data
  })
}

export function getSystemSettings() {
  return request({
    url: '/admin/system/settings',
    method: 'get'
  })
}

export function updateSystemSettings(data) {
  return request({
    url: '/admin/system/settings',
    method: 'put',
    data
  })
}

export function getSystemInfo() {
  return request({
    url: '/admin/system/config/info',
    method: 'get'
  })
}
