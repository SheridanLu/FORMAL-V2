import request from '@/utils/request'

export function uploadAttachment(file, bizType, bizId) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('bizType', bizType)
  formData.append('bizId', bizId)
  return request.post('/api/v1/attachments/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getAttachmentList(params) {
  return request.get('/api/v1/attachments', { params })
}

export function getAttachmentsByBiz(bizType, bizId) {
  return request.get('/api/v1/attachments/biz', { params: { bizType, bizId } })
}

export function getAttachmentUrl(id) {
  return request.get(`/api/v1/attachments/${id}/url`)
}

export function deleteAttachment(id) {
  return request.delete(`/api/v1/attachments/${id}`)
}
