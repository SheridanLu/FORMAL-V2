import request from '@/utils/request'

export function getProjectList(params) {
  return request.get('/api/v1/projects', { params })
}

export function getAllProjects() {
  return request.get('/api/v1/projects/all')
}

export function getProjectById(id) {
  return request.get(`/api/v1/projects/${id}`)
}

export function createProject(data) {
  return request.post('/api/v1/projects', data)
}

export function updateProject(id, data) {
  return request.put(`/api/v1/projects/${id}`, data)
}

export function updateProjectStatus(id, status) {
  return request.patch(`/api/v1/projects/${id}/status`, { status })
}

export function deleteProject(id) {
  return request.delete(`/api/v1/projects/${id}`)
}

// V3.2 新增项目操作
export function convertProject(id) {
  return request.post(`/api/v1/projects/${id}/convert`)
}

export function terminateProject(id, data) {
  return request.post(`/api/v1/projects/${id}/terminate`, data)
}

export function suspendProject(id, data) {
  return request.post(`/api/v1/projects/${id}/suspend`, data)
}

export function resumeProject(id) {
  return request.post(`/api/v1/projects/${id}/resume`)
}

// 项目成员
export function getProjectMembers(projectId) {
  return request.get(`/api/v1/projects/${projectId}/members`)
}

export function addProjectMember(projectId, data) {
  return request.post(`/api/v1/projects/${projectId}/members`, data)
}

export function removeProjectMember(projectId, memberId) {
  return request.delete(`/api/v1/projects/${projectId}/members/${memberId}`)
}
