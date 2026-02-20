import request from '@/utils/request'

export function getSchoolList() {
  return request({
    url: '/school/list',
    method: 'get'
  }).catch(() => ({ data: [] }))
}
