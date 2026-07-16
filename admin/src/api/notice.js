import request from '@/utils/request'

const api = {
  getNoticeList: '/v1/notice/notices/:projectId',
  createNotice: '/v1/notice/notice',
  updateNotice: '/v1/notice/notice',
  deleteNotice: '/v1/notice/notice'
}

export default api

// 查询通知列表
export function getNoticeList (id, params) {
  return request({
    url: api.getNoticeList.replace(':projectId', id),
    method: 'get',
    params
  })
}

// 创建通知
export function createNotice (params) {
  return request({
    url: api.createNotice,
    method: 'post',
    data: params
  })
}

// 更新通知
export function updateNotice (params) {
  return request({
    url: `${api.updateNotice}/${params.id}`,
    method: 'put',
    data: params
  })
}

// 删除通知
export function deleteNotice (projectId) {
  return request({
    url: `${api.deleteNotice}/${projectId}`,
    method: 'delete'
  })
}
