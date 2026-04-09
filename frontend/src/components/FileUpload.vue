<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :file-list="fileList"
      :limit="limit"
      :accept="accept"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
      :on-preview="handlePreview"
      v-bind="$attrs"
    >
      <el-button type="primary" size="small"><el-icon><Upload /></el-icon>上传文件</el-button>
      <template #tip>
        <div class="el-upload__tip" v-if="tip">{{ tip }}</div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Upload } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  limit: { type: Number, default: 10 },
  maxSize: { type: Number, default: 50 }, // MB
  accept: { type: String, default: '' },
  tip: { type: String, default: '' },
  bizType: { type: String, default: '' },
  bizId: { type: [String, Number], default: '' }
})
const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/v1/attachments/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}))

const fileList = computed(() =>
  (props.modelValue || []).map(f => ({
    name: f.file_name || f.name,
    url: f.file_url || f.url,
    id: f.id
  }))
)

function beforeUpload(file) {
  if (props.maxSize && file.size / 1024 / 1024 > props.maxSize) {
    ElMessage.warning(`文件大小不能超过${props.maxSize}MB`)
    return false
  }
  return true
}

function handleSuccess(response, file) {
  if (response.code === 0) {
    const newList = [...(props.modelValue || []), response.data]
    emit('update:modelValue', newList)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleRemove(file) {
  const newList = (props.modelValue || []).filter(f => f.id !== file.id)
  emit('update:modelValue', newList)
}

function handleExceed() {
  ElMessage.warning(`最多上传${props.limit}个文件`)
}

function handlePreview(file) {
  if (file.url) window.open(file.url, '_blank')
}
</script>
