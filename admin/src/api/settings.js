import request from '@/utils/request'

export function getConfigList() {
  return request({
    url: '/admin/system/config/list',
    method: 'get'
  })
}

export function updateConfig(configKey, configValue) {
  return request({
    url: '/admin/system/config/update',
    method: 'put',
    data: { configKey, configValue }
  })
}
