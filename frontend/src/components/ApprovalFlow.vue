<template>
  <el-dialog :model-value="visible" @update:model-value="$emit('update:visible', $event)" title="审批" width="600px" destroy-on-close>
    <div v-loading="loading">
      <!-- 审批时间线 -->
      <approval-timeline v-if="detail" :records="detail.records || []" :nodes="detail.nodes || []" />

      <!-- 审批操作 -->
      <el-divider />
      <el-form v-if="canOperate" :model="form" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="submitting" @click="doApprove">通过</el-button>
          <el-button type="danger" :loading="submitting" @click="doReject">驳回</el-button>
          <el-button @click="$emit('update:visible', false)">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getInstanceDetail, approveInstance, rejectInstance } from '@/api/approval'

const props = defineProps({
  visible: Boolean,
  instanceId: { type: [String, Number], default: '' }
})
const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const submitting = ref(false)
const detail = ref(null)
const canOperate = ref(false)
const form = ref({ comment: '' })

async function fetchDetail() {
  if (!props.instanceId) return
  loading.value = true
  try {
    const res = await getInstanceDetail(props.instanceId)
    detail.value = res.data
    canOperate.value = res.data?.can_operate ?? false
  } finally {
    loading.value = false
  }
}

async function doApprove() {
  submitting.value = true
  try {
    await approveInstance(props.instanceId, { comment: form.value.comment })
    ElMessage.success('审批通过')
    emit('update:visible', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}

async function doReject() {
  submitting.value = true
  try {
    await rejectInstance(props.instanceId, { comment: form.value.comment })
    ElMessage.success('已驳回')
    emit('update:visible', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    form.value.comment = ''
    fetchDetail()
  }
})
</script>
