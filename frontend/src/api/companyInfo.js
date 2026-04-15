import request from '@/utils/request'

/**
 * 公司/单位信息 API
 * V3.0 §3.5: 提供公司信息查询与自动填充
 */

/** 分页列表 */
export function getCompanyInfoList(params) {
  return request.get('/api/v1/company-info', { params })
}

/** 模糊搜索（自动填充下拉） */
export function searchCompanyInfo(keyword) {
  return request.get('/api/v1/company-info/search', { params: { keyword } })
}

/** 按名称精确查找 */
export function getCompanyInfoByName(name) {
  return request.get('/api/v1/company-info/by-name', { params: { name } })
}

/** 详情 */
export function getCompanyInfo(id) {
  return request.get(`/api/v1/company-info/${id}`)
}

/** 新增 */
export function createCompanyInfo(data) {
  return request.post('/api/v1/company-info', data)
}

/** 更新 */
export function updateCompanyInfo(id, data) {
  return request.put(`/api/v1/company-info/${id}`, data)
}

/** 删除 */
export function deleteCompanyInfo(id) {
  return request.delete(`/api/v1/company-info/${id}`)
}
