import request from '@/utils/request'

export function getTodoList(params) {
  return request.get('/api/v1/todos', { params })
}

export function getTodoDetail(id) {
  return request.get(`/api/v1/todos/${id}`)
}

export function getTodoCount() {
  return request.get('/api/v1/todos/count')
}

export function markTodoDone(id) {
  return request.patch(`/api/v1/todos/${id}/done`)
}
