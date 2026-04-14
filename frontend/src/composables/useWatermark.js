import { onBeforeUnmount } from 'vue'

export function useWatermark() {
  let watermarkDiv = null
  let observer = null

  /**
   * 设置水印
   * @param {string} text 水印文本（如用户名）
   * @param {HTMLElement} container 容器元素，默认 document.body
   */
  function setWatermark(text, container = document.body) {
    clearWatermark()

    const canvas = document.createElement('canvas')
    canvas.width = 300
    canvas.height = 200
    const ctx = canvas.getContext('2d')

    ctx.rotate((-30 * Math.PI) / 180)
    ctx.font = '14px Arial'
    ctx.fillStyle = 'rgba(180, 180, 180, 0.2)'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'

    const date = new Date().toLocaleDateString('zh-CN')
    ctx.fillText(text, 120, 120)
    ctx.fillText(date, 120, 145)

    watermarkDiv = document.createElement('div')
    watermarkDiv.id = 'mochu-watermark'
    watermarkDiv.style.cssText = [
      'position: fixed',
      'top: 0',
      'left: 0',
      'width: 100%',
      'height: 100%',
      'z-index: 99999',
      'pointer-events: none',
      `background-image: url(${canvas.toDataURL('image/png')})`,
      'background-repeat: repeat'
    ].join(';')

    container.appendChild(watermarkDiv)

    // #14 fix: 保存原始样式，防止 transform/z-index/background-image 等方式绕过
    const originalCssText = watermarkDiv.style.cssText

    // MutationObserver 防删除
    observer = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        // 水印被删除时重新添加
        if (mutation.type === 'childList') {
          const removed = Array.from(mutation.removedNodes)
          if (removed.includes(watermarkDiv)) {
            container.appendChild(watermarkDiv)
          }
        }
        // 水印属性被修改时恢复完整原始样式
        if (mutation.type === 'attributes' && mutation.target === watermarkDiv) {
          watermarkDiv.style.cssText = originalCssText
        }
      }
    })

    observer.observe(container, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['style', 'class']
    })
  }

  function clearWatermark() {
    if (observer) {
      observer.disconnect()
      observer = null
    }
    if (watermarkDiv && watermarkDiv.parentNode) {
      watermarkDiv.parentNode.removeChild(watermarkDiv)
      watermarkDiv = null
    }
  }

  onBeforeUnmount(() => {
    clearWatermark()
  })

  return { setWatermark, clearWatermark }
}
