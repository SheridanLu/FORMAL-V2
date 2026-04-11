import request from '@/utils/request'

// ==================== 流程定义 ====================

export function getProcessDefList(params) {
  return request({ url: '/api/v1/bpm/process-def', method: 'get', params })
}

export function deployProcess(data) {
  return request({ url: '/api/v1/bpm/process-def/deploy', method: 'post', data })
}

export function getProcessBpmnXml(processDefId) {
  return request({ url: `/api/v1/bpm/process-def/${processDefId}/xml`, method: 'get' })
}

export function suspendProcess(processDefId) {
  return request({ url: `/api/v1/bpm/process-def/${processDefId}/suspend`, method: 'put' })
}

export function activateProcess(processDefId) {
  return request({ url: `/api/v1/bpm/process-def/${processDefId}/activate`, method: 'put' })
}

export function deleteProcessDef(processDefId) {
  return request({ url: `/api/v1/bpm/process-def/${processDefId}`, method: 'delete' })
}

export function updateProcessExt(processDefId, data) {
  return request({ url: `/api/v1/bpm/process-def/${processDefId}/ext`, method: 'put', data })
}

// ==================== 流程任务 ====================

export function getTodoTasks(params) {
  return request({ url: '/api/v1/bpm/task/todo', method: 'get', params })
}

export function claimTask(taskId) {
  return request({ url: `/api/v1/bpm/task/${taskId}/claim`, method: 'post', data: {} })
}

export function completeTask(taskId, data) {
  return request({ url: `/api/v1/bpm/task/${taskId}/complete`, method: 'post', data })
}

export function rejectTask(taskId, data) {
  return request({ url: `/api/v1/bpm/task/${taskId}/reject`, method: 'post', data })
}

export function transferTask(taskId, data) {
  return request({ url: `/api/v1/bpm/task/${taskId}/transfer`, method: 'post', data })
}

export function delegateTask(taskId, data) {
  return request({ url: `/api/v1/bpm/task/${taskId}/delegate`, method: 'post', data })
}

export function withdrawProcess(processInstId, reason) {
  return request({ url: `/api/v1/bpm/task/withdraw/${processInstId}`, method: 'post', params: { reason } })
}

// ==================== 流程实例 ====================

export function startProcess(data) {
  return request({ url: '/api/v1/bpm/instance/start', method: 'post', data })
}

export function getMyInstances(params) {
  return request({ url: '/api/v1/bpm/instance/my', method: 'get', params })
}

export function getAllInstances(params) {
  return request({ url: '/api/v1/bpm/instance', method: 'get', params })
}

export function getInstanceDetail(processInstId) {
  return request({ url: `/api/v1/bpm/instance/${processInstId}`, method: 'get' })
}

export function getHighlightNodes(processInstId) {
  return request({ url: `/api/v1/bpm/instance/${processInstId}/highlight`, method: 'get' })
}

// ==================== OA 规则 ====================

export function getOaRules() {
  return request({ url: '/api/v1/bpm/instance/rules', method: 'get' })
}

export function saveOaRule(data) {
  return request({ url: '/api/v1/bpm/instance/rules', method: 'post', data })
}

export function deleteOaRule(id) {
  return request({ url: `/api/v1/bpm/instance/rules/${id}`, method: 'delete' })
}
