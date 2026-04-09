/**
 * 文件下载工具
 */

/**
 * 通过 URL 下载文件
 */
export function downloadByUrl(url, filename = '') {
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.style.display = 'none'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

/**
 * 通过 Blob 下载文件
 */
export function downloadByBlob(data, filename, mimeType = 'application/octet-stream') {
  const blob = data instanceof Blob ? data : new Blob([data], { type: mimeType })
  const url = URL.createObjectURL(blob)
  downloadByUrl(url, filename)
  URL.revokeObjectURL(url)
}

/**
 * 通过 API 响应下载（axios responseType: 'blob'）
 */
export function downloadByResponse(response) {
  const contentDisposition = response.headers?.['content-disposition'] || ''
  const filenameMatch = contentDisposition.match(/filename\*?=(?:UTF-8'')?(.+)/i)
  const filename = filenameMatch ? decodeURIComponent(filenameMatch[1]) : 'download'
  downloadByBlob(response.data, filename)
}
