import request from '@/utils/request'

export function getCompletionList(params) {
  return request.get('/api/v1/completion/finish', { params })
}

export function createCompletion(data) {
  return request.post('/api/v1/completion/finish', data)
}

export function updateCompletion(id, data) {
  return request.put(`/api/v1/completion/finish/${id}`, data)
}

export function deleteCompletion(id) {
  return request.delete(`/api/v1/completion/finish/${id}`)
}

export function getLaborList(params) {
  return request.get('/api/v1/completion/labor', { params })
}

export function createLabor(data) {
  return request.post('/api/v1/completion/labor', data)
}

export function updateLabor(id, data) {
  return request.put(`/api/v1/completion/labor/${id}`, data)
}

export function deleteLabor(id) {
  return request.delete(`/api/v1/completion/labor/${id}`)
}

export function getCaseList(params) {
  return request.get('/api/v1/completion/cases', { params })
}

export function createCase(data) {
  return request.post('/api/v1/completion/cases', data)
}

export function updateCase(id, data) {
  return request.put(`/api/v1/completion/cases/${id}`, data)
}

export function deleteCase(id) {
  return request.delete(`/api/v1/completion/cases/${id}`)
}

export function getExceptionList(params) {
  return request.get('/api/v1/completion/exceptions', { params })
}

export function createException(data) {
  return request.post('/api/v1/completion/exceptions', data)
}

export function deleteException(id) {
  return request.delete(`/api/v1/completion/exceptions/${id}`)
}

// V3.2 新增 - 竣工图纸
export function getDrawingList(params) {
  return request.get('/api/v1/completion/drawings', { params })
}

export function uploadDrawing(data) {
  return request.post('/api/v1/completion/drawings', data)
}

export function deleteDrawing(id) {
  return request.delete(`/api/v1/completion/drawings/${id}`)
}

// 竣工文档
export function getCompletionDocList(params) {
  return request.get('/api/v1/completion/documents', { params })
}

export function uploadCompletionDoc(data) {
  return request.post('/api/v1/completion/documents', data)
}

// 提交操作
export function submitCompletion(id) {
  return request.post(`/api/v1/completion/finish/${id}/submit`)
}

export function submitLabor(id) {
  return request.post(`/api/v1/completion/labor/${id}/submit`)
}
