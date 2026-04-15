import request from '@/utils/request'

export function getContractList(params) {
  return request.get('/api/v1/contracts', { params })
}

export function getContractById(id) {
  return request.get(`/api/v1/contracts/${id}`)
}

export function createContract(data) {
  return request.post('/api/v1/contracts', data)
}

export function updateContract(id, data) {
  return request.put(`/api/v1/contracts/${id}`, data)
}

export function updateContractStatus(id, status) {
  return request.patch(`/api/v1/contracts/${id}/status`, { status })
}

export function deleteContract(id) {
  return request.delete(`/api/v1/contracts/${id}`)
}

// V3.2 新增
export function submitContract(id) {
  return request.post(`/api/v1/contracts/${id}/submit`)
}

export function terminateContract(id, data) {
  return request.post(`/api/v1/contracts/${id}/terminate`, data)
}

export function createSupplement(contractId, data) {
  return request.post(`/api/v1/contracts/${contractId}/supplements`, data)
}

export function getSupplements(contractId) {
  return request.get(`/api/v1/contracts/${contractId}/supplements`)
}

export function checkOverquantity(contractId) {
  return request.get(`/api/v1/contracts/${contractId}/overquantity-check`)
}

// 合同关联的付款
export function getContractPayments(contractId, params) {
  return request.get(`/api/v1/contracts/${contractId}/payments`, { params })
}

// 合同关联的发票
export function getContractInvoices(contractId, params) {
  return request.get(`/api/v1/contracts/${contractId}/invoices`, { params })
}

// V3.0: 合同付款计划
export function getPaymentPlans(contractId) {
  return request.get(`/api/v1/contracts/${contractId}/payment-plans`)
}

export function createPaymentPlans(contractId, data) {
  return request.post(`/api/v1/contracts/${contractId}/payment-plans`, data)
}
