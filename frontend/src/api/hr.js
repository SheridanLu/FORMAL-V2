import request from '@/utils/request'

export function getSalaryList(params) {
  return request.get('/api/v1/hr/salaries', { params })
}

export function createSalary(data) {
  return request.post('/api/v1/hr/salaries', data)
}

export function updateSalary(id, data) {
  return request.put(`/api/v1/hr/salaries/${id}`, data)
}

export function deleteSalary(id) {
  return request.delete(`/api/v1/hr/salaries/${id}`)
}

export function getHrContractList(params) {
  return request.get('/api/v1/hr/contracts', { params })
}

export function createHrContract(data) {
  return request.post('/api/v1/hr/contracts', data)
}

export function updateHrContract(id, data) {
  return request.put(`/api/v1/hr/contracts/${id}`, data)
}

export function deleteHrContract(id) {
  return request.delete(`/api/v1/hr/contracts/${id}`)
}

export function getCertificateList(params) {
  return request.get('/api/v1/hr/certificates', { params })
}

export function createCertificate(data) {
  return request.post('/api/v1/hr/certificates', data)
}

export function updateCertificate(id, data) {
  return request.put(`/api/v1/hr/certificates/${id}`, data)
}

export function deleteCertificate(id) {
  return request.delete(`/api/v1/hr/certificates/${id}`)
}

export function getEntryList(params) {
  return request.get('/api/v1/hr/entries', { params })
}

export function createEntry(data) {
  return request.post('/api/v1/hr/entries', data)
}

export function updateEntry(id, data) {
  return request.put(`/api/v1/hr/entries/${id}`, data)
}

export function deleteEntry(id) {
  return request.delete(`/api/v1/hr/entries/${id}`)
}

export function getResignList(params) {
  return request.get('/api/v1/hr/resigns', { params })
}

export function createResign(data) {
  return request.post('/api/v1/hr/resigns', data)
}

export function updateResign(id, data) {
  return request.put(`/api/v1/hr/resigns/${id}`, data)
}

export function deleteResign(id) {
  return request.delete(`/api/v1/hr/resigns/${id}`)
}

// V3.2 新增 - 薪资配置
export function getSalaryConfigList(params) {
  return request.get('/api/v1/hr/salary-config', { params })
}

export function createSalaryConfig(data) {
  return request.post('/api/v1/hr/salary-config', data)
}

export function updateSalaryConfig(id, data) {
  return request.put(`/api/v1/hr/salary-config/${id}`, data)
}

// 社保配置
export function getSocialInsuranceConfig(params) {
  return request.get('/api/v1/hr/social-insurance-config', { params })
}

export function updateSocialInsuranceConfig(data) {
  return request.put('/api/v1/hr/social-insurance-config', data)
}

// 个税税率表
export function getTaxRateTable() {
  return request.get('/api/v1/hr/tax-rate-table')
}

export function updateTaxRateTable(data) {
  return request.put('/api/v1/hr/tax-rate-table', data)
}

// 提交操作
export function submitSalary(id) {
  return request.post(`/api/v1/hr/salaries/${id}/submit`)
}

export function submitEntry(id) {
  return request.post(`/api/v1/hr/entries/${id}/submit`)
}

export function submitResign(id) {
  return request.post(`/api/v1/hr/resigns/${id}/submit`)
}

// 资产移交
export function getAssetTransferList(params) {
  return request.get('/api/v1/hr/asset-transfers', { params })
}

export function createAssetTransfer(data) {
  return request.post('/api/v1/hr/asset-transfers', data)
}

export function submitAssetTransfer(id) {
  return request.post(`/api/v1/hr/asset-transfers/${id}/submit`)
}
