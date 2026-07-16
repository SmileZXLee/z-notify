import request from '@/utils/request'

const api = {
  getStatisticsList: '/v1/statistics/statistics/:projectId',
  getStatisticsAnalysis: '/v1/statistics/analysis/:projectId'
}

export default api

// 查询访问统计列表
export function getStatisticsList (id, params) {
  return request({
    url: api.getStatisticsList.replace(':projectId', id),
    method: 'get',
    params
  })
}

// 查询访问统计分析
export function getStatisticsAnalysis (id, params) {
  return request({
    url: api.getStatisticsAnalysis.replace(':projectId', id),
    method: 'get',
    params
  })
}
