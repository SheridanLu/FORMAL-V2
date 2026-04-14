import request from '@/utils/request'

// ====== 枚举 ======
export function getContractTypes() {
  return request.get('/api/v1/enums/contract-types')
}

// ====== 模板 CRUD ======
export function getTplList(params) {
  return request.get('/api/v1/contract-tpls', { params })
}

export function getTplById(id) {
  return request.get(`/api/v1/contract-tpls/${id}`)
}

export function createTpl(data) {
  return request.post('/api/v1/contract-tpls', data)
}

export function createTplWithFile(contractType, tplName, description, file) {
  const formData = new FormData()
  formData.append('contractType', contractType)
  formData.append('tplName', tplName)
  if (description) formData.append('description', description)
  formData.append('file', file)
  return request.post('/api/v1/contract-tpls/with-file', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateTpl(id, data) {
  return request.put(`/api/v1/contract-tpls/${id}`, data)
}

export function deleteTpl(id) {
  return request.delete(`/api/v1/contract-tpls/${id}`)
}

// ====== 版本管理 ======
export function uploadTplVersion(tplId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/v1/contract-tpls/${tplId}/versions`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getTplVersions(tplId) {
  return request.get(`/api/v1/contract-tpls/${tplId}/versions`)
}

export function getTplVersion(versionId) {
  return request.get(`/api/v1/contract-tpls/versions/${versionId}`)
}

export function updateVersionStatus(versionId, status) {
  return request.patch(`/api/v1/contract-tpls/versions/${versionId}/status`, { status })
}

export function submitVersionApproval(versionId) {
  return request.post(`/api/v1/contract-tpls/versions/${versionId}/submit-approval`)
}

export function previewVersion(versionId) {
  return request.get(`/api/v1/contract-tpls/versions/${versionId}/preview`)
}

export function downloadVersion(versionId) {
  return request.get(`/api/v1/contract-tpls/versions/${versionId}/download`)
}

// ====== 字段管理 ======
export function getVersionFields(versionId) {
  return request.get(`/api/v1/contract-tpls/versions/${versionId}/fields`)
}

export function updateVersionFields(versionId, fields) {
  return request.put(`/api/v1/contract-tpls/versions/${versionId}/fields`, { fields })
}

// ====== 审计日志 ======
export function getTplAuditLogs(tplId, params) {
  return request.get(`/api/v1/contract-tpls/${tplId}/audit-logs`, { params })
}

// ====== 合同创建时查询启用模板 ======
export function getActiveTplVersion(contractType) {
  return request.get('/api/v1/contract-tpls/active', { params: { contractType } })
}
