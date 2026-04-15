import request from '@/utils/request'

/** 管理端分页列表 */
export function getBannerList(params) {
  return request.get('/api/v1/banners', { params })
}

/** 前台展示列表 */
export function getActiveBanners() {
  return request.get('/api/v1/banners/active')
}

/** 详情 */
export function getBanner(id) {
  return request.get(`/api/v1/banners/${id}`)
}

/** 新增 */
export function createBanner(data) {
  return request.post('/api/v1/banners', data)
}

/** 更新 */
export function updateBanner(id, data) {
  return request.put(`/api/v1/banners/${id}`, data)
}

/** 删除 */
export function deleteBanner(id) {
  return request.delete(`/api/v1/banners/${id}`)
}

/** 切换启用/禁用 */
export function toggleBanner(id) {
  return request.patch(`/api/v1/banners/${id}/toggle`)
}
