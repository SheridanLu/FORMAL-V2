import { ref, reactive } from 'vue'

/**
 * 通用表单提交 composable
 * @param {Function} createApi - 创建 API
 * @param {Function} updateApi - 更新 API（可选）
 * @param {Object} defaultForm - 默认表单数据
 */
export function useForm(createApi, updateApi = null, defaultForm = {}) {
  const formRef = ref(null)
  const submitting = ref(false)
  const formVisible = ref(false)
  const isEdit = ref(false)
  const form = reactive({ ...defaultForm })

  function openCreate() {
    isEdit.value = false
    Object.assign(form, { ...defaultForm })
    formVisible.value = true
  }

  function openEdit(row) {
    isEdit.value = true
    Object.assign(form, { ...row })
    formVisible.value = true
  }

  async function handleSubmit(onSuccess) {
    if (formRef.value) {
      await formRef.value.validate()
    }
    submitting.value = true
    try {
      if (isEdit.value && updateApi) {
        await updateApi(form.id, form)
      } else {
        await createApi(form)
      }
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      formVisible.value = false
      if (onSuccess) onSuccess()
    } finally {
      submitting.value = false
    }
  }

  return {
    formRef,
    submitting,
    formVisible,
    isEdit,
    form,
    openCreate,
    openEdit,
    handleSubmit
  }
}
