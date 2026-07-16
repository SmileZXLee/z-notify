import request from '@/utils/request'

const api = {
  getTextList: '/v1/text/texts/:projectId',
  createText: '/v1/text/text',
  updateText: '/v1/text/text',
  deleteText: '/v1/text/text'
}

export default api

// 查询文本列表
export function getTextList (id, params) {
  return request({
    url: api.getTextList.replace(':projectId', id),
    method: 'get',
    params
  })
}

// 创建文本
export function createText (params) {
  return request({
    url: api.createText,
    method: 'post',
    data: params
  })
}

// 更新文本
export function updateText (params) {
  return request({
    url: `${api.updateText}/${params.id}`,
    method: 'put',
    data: params
  })
}

// 删除文本
export function deleteText (projectId) {
  return request({
    url: `${api.deleteText}/${projectId}`,
    method: 'delete'
  })
}
