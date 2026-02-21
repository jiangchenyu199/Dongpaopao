import request from '@/utils/request'

export function getSplash() {
  return request({
    url: '/admin/app/splash',
    method: 'get'
  })
}

export function saveSplash(data) {
  return request({
    url: '/admin/app/splash',
    method: 'post',
    data
  })
}

export function getNoticeList() {
  return request({
    url: '/admin/app/notice/list',
    method: 'get'
  })
}

export function saveNotice(data) {
  return request({
    url: '/admin/app/notice/save',
    method: 'post',
    data
  })
}

export function deleteNotice(id) {
  return request({
    url: `/admin/app/notice/delete/${id}`,
    method: 'delete'
  })
}

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
