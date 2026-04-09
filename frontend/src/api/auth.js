import request from '@/utils/request'

/**
 * 认证相关 API — 对照 V3.2 §4.1
 */

export function checkAccount(username) {
  return request.post('/api/v1/auth/check-account', { username })
}

export function sendSms(phone) {
  return request.post(`/api/v1/auth/send-sms?phone=${encodeURIComponent(phone)}`)
}

export function loginByPassword(data) {
  return request.post('/api/v1/auth/login-by-password', data)
}

export function loginBySms(data) {
  return request.post('/api/v1/auth/login-by-sms', {
    phone: data.phone,
    smsCode: data.smsCode
  })
}

export function logout() {
  return request.post('/api/v1/auth/logout')
}

export function forgotPassword(phone) {
  return request.post(`/api/v1/auth/forgot-password?phone=${encodeURIComponent(phone)}`)
}

export function resetPassword(data) {
  return request.post('/api/v1/auth/reset-password', {
    phone: data.phone,
    smsCode: data.smsCode,
    newPassword: data.newPassword
  })
}

export function retrieveAccount(phone) {
  return request.post(`/api/v1/auth/retrieve-account?phone=${encodeURIComponent(phone)}`)
}

export function getCurrentUser() {
  return request.get('/api/v1/user/me')
}

export function getHomeData() {
  return request.get('/api/v1/home')
}
