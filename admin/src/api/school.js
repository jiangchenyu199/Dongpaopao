import request from '@/utils/request'

export function getSchoolList() {
  return request({
    url: '/admin/school/list',
    method: 'get'
  }).catch(() => ({ data: [] }))
}

export function saveSchool(school) {
  return request({
    url: '/admin/school/save',
    method: 'post',
    data: school
  })
}

export function deleteSchool(sid) {
  return request({
    url: `/admin/school/delete/${sid}`,
    method: 'delete'
  })
}
