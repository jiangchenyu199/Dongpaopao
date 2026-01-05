import request from '@/utils/request'

export function getStatisticsList() {
  return request({
    url: '/admin/statistics/list',
    method: 'get'
  })
}

export function getLatestStatistics() {
  return request({
    url: '/admin/statistics/latest',
    method: 'get'
  })
}

export function getDashboardCharts() {
  return request({
    url: '/admin/statistics/dashboard',
    method: 'get'
  })
}
