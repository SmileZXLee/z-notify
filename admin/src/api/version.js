import request from '@/utils/request'

const api = {
  getVersionList: '/v1/version/versions/:projectId',
  createVersion: '/v1/version/version',
  updateVersion: '/v1/version/version',
  deleteVersion: '/v1/version/version'
}

export default api

// 查询版本列表
export function getVersionList (id, params) {
  return request({
    url: api.getVersionList.replace(':projectId', id),
    method: 'get',
    params
  })
}

// 创建版本
export function createVersion (params) {
  return request({
    url: api.createVersion,
    method: 'post',
    data: params
  })
}

// 更新版本
export function updateVersion (params) {
  return request({
    url: `${api.updateVersion}/${params.id}`,
    method: 'put',
    data: params
  })
}

// 删除版本
export function deleteVersion (projectId) {
  return request({
    url: `${api.deleteVersion}/${projectId}`,
    method: 'delete'
  })
}
