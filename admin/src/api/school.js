import request from '@/utils/request'

// 学校列表（使用 C 端接口， admin 可共用）
export function getSchoolList() {
  return request({
    url: '/school/list',
    method: 'get'
  }).catch(() => ({ data: [] }))
}
