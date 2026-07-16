const http = uni.$u.http
const api = {};

// 发送反馈
api.feedback = (params) => http.post('/v1/public/feedback/feedback', params)
// 查询某个用户下的反馈列表
api.getFeedbackList = (projectId, username) => http.get(`/v1/public/feedbacks/${projectId}/${username}`)

module.exports = api;
