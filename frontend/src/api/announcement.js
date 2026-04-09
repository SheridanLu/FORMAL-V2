import request from '@/utils/request'

/**
 * 公告管理 API
 */

export function getAnnouncementList(params) {
  return request.get('/api/v1/admin/announcements', { params })
}

export function getAnnouncementDetail(id) {
  return request.get(`/api/v1/admin/announcements/${id}`)
}

export function createAnnouncement(data) {
  return request.post('/api/v1/admin/announcements', data)
}

export function updateAnnouncement(id, data) {
  return request.put(`/api/v1/admin/announcements/${id}`, data)
}

export function publishAnnouncement(id) {
  return request.patch(`/api/v1/admin/announcements/${id}/publish`)
}

export function offlineAnnouncement(id) {
  return request.patch(`/api/v1/admin/announcements/${id}/offline`)
}

export function toggleTopAnnouncement(id) {
  return request.patch(`/api/v1/admin/announcements/${id}/toggle-top`)
}

export function deleteAnnouncement(id) {
  return request.delete(`/api/v1/admin/announcements/${id}`)
}

export function getPublishedAnnouncements(limit = 10) {
  return request.get('/api/v1/announcements', { params: { limit } })
}
