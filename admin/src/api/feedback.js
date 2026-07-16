import request from '@/utils/request'

const api = {
  getFeedbackList: '/v1/feedback/feedbacks/:projectId',
  replyFeedback: '/v1/feedback/reply/:id',
  deleteFeedback: '/v1/feedback/feedback/:id'
}

export default api

// 查询反馈列表
export function getFeedbackList (projectId, params) {
  return request({
    url: api.getFeedbackList.replace(':projectId', projectId),
    method: 'get',
    params
  })
}

// 回复用户反馈
export function replyFeedback (id, params) {
  return request({
    url: api.replyFeedback.replace(':id', id),
    method: 'put',
    data: params
  })
}

// 删除用户反馈
export function deleteFeedback (id) {
  return request({
    url: api.deleteFeedback.replace(':id', id),
    method: 'delete'
  })
}
