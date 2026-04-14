import request from '@/utils/request'

export function getUserShortcuts() {
  return request.get('/api/v1/user/shortcuts')
}

export function updateUserShortcuts(data) {
  return request.put('/api/v1/user/shortcuts', data)
}

export function getAvailableMenus() {
  return request.get('/api/v1/user/shortcuts/available-menus')
}
