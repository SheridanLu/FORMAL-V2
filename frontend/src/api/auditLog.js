import request from '@/utils/request'

export function getAuditLogList(params) {
  return request.get('/api/v1/admin/audit-logs', { params })
}
