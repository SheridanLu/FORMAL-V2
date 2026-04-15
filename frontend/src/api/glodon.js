import request from '@/utils/request'

/** 导入记录列表 */
export function getGlodonImportList(params) {
  return request.get('/api/v1/glodon/imports', { params })
}

/** 上传并导入文件 */
export function importGlodonFile(projectId, importType, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/v1/glodon/import?projectId=${projectId}&importType=${importType}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 删除导入记录 */
export function deleteGlodonImport(id) {
  return request.delete(`/api/v1/glodon/imports/${id}`)
}
