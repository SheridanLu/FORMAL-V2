import request from '@/utils/request'

export function getDocumentList(params) {
  return request.get('/api/v1/documents', { params })
}

export function getDocument(id) {
  return request.get(`/api/v1/documents/${id}`)
}

export function uploadDocument(file, category, projectId) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('category', category || 'other')
  if (projectId) formData.append('project_id', projectId)
  return request.post('/api/v1/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateDocument(id, data) {
  return request.put(`/api/v1/documents/${id}`, data)
}

export function deleteDocument(id) {
  return request.delete(`/api/v1/documents/${id}`)
}
