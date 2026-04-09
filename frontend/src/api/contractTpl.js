import request from '@/utils/request'

// ====== 枚举 ======
export function getContractTypes() {
  return request.get('/api/v1/enums/contract-types')
}

// ====== 模板 CRUD ======
export function getTplList(params) {
  return request.get('/api/v1/contract-tpl', { params })
}

export function getTplById(id) {
  return request.get(`/api/v1/contract-tpl/${id}`)
}

export function createTpl(data) {
  return request.post('/api/v1/contract-tpl', data)
}

export function createTplWithFile(contractType, tplName, description, file) {
  const formData = new FormData()
  formData.append('contractType', contractType)
  formData.append('tplName', tplName)
  if (description) formData.append('description', description)
  formData.append('file', file)
  return request.post('/api/v1/contract-tpl/with-file', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateTpl(id, data) {
  return request.put(`/api/v1/contract-tpl/${id}`, data)
}

export function deleteTpl(id) {
  return request.delete(`/api/v1/contract-tpl/${id}`)
}

// ====== 版本管理 ======
export function uploadTplVersion(tplId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/v1/contract-tpl/${tplId}/versions`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getTplVersions(tplId) {
  return request.get(`/api/v1/contract-tpl/${tplId}/versions`)
}

export function getTplVersion(versionId) {
  return request.get(`/api/v1/contract-tpl/versions/${versionId}`)
}

export function updateVersionStatus(versionId, status) {
  return request.patch(`/api/v1/contract-tpl/versions/${versionId}/status`, { status })
}

export function submitVersionApproval(versionId) {
  return request.post(`/api/v1/contract-tpl/versions/${versionId}/submit-approval`)
}

export function previewVersion(versionId) {
  return request.get(`/api/v1/contract-tpl/versions/${versionId}/preview`)
}

export function downloadVersion(versionId) {
  return request.get(`/api/v1/contract-tpl/versions/${versionId}/download`)
}

// ====== 字段管理 ======
export function getVersionFields(versionId) {
  return request.get(`/api/v1/contract-tpl/versions/${versionId}/fields`)
}

export function updateVersionFields(versionId, fields) {
  return request.put(`/api/v1/contract-tpl/versions/${versionId}/fields`, { fields })
}

// ====== 审计日志 ======
export function getTplAuditLogs(tplId, params) {
  return request.get(`/api/v1/contract-tpl/${tplId}/audit-logs`, { params })
}

// ====== 合同创建时查询启用模板 ======
export function getActiveTplVersion(contractType) {
  return request.get('/api/v1/contract-tpl/active', { params: { contractType } })
}
