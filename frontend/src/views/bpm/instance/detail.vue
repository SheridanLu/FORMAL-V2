<template>
  <el-dialog v-model="visible" title="流程实例详情" width="700px" destroy-on-close>
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="detail">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 16px;">
        <el-descriptions-item label="实例ID">{{ detail.process_inst_id }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detail.biz_type }}</el-descriptions-item>
        <el-descriptions-item label="业务单号">{{ detail.biz_no }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ detail.initiator_name }}</el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ detail.start_time }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.end_time || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="resultType(detail.result)" size="small">{{ resultLabel(detail.result) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div style="font-weight: 600; margin-bottom: 8px;">审批记录</div>
      <el-timeline>
        <el-timeline-item
          v-for="r in detail.records"
          :key="r.task_id"
          :timestamp="r.end_time || r.start_time"
          placement="top"
        >
          <el-card shadow="never" style="padding: 8px 16px;">
            <div style="font-weight: 600;">{{ r.task_name }} — {{ r.assignee_name || r.assignee_id }}</div>
            <div v-if="r.comment" style="color: #666; margin-top: 4px;">意见：{{ r.comment }}</div>
            <div v-if="!r.end_time" style="color: #e6a23c; margin-top: 4px;">（处理中）</div>
          </el-card>
        </el-timeline-item>
        <el-timeline-item v-if="!detail.records?.length" timestamp="">
          <span style="color: #999;">暂无审批记录</span>
        </el-timeline-item>
      </el-timeline>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { getInstanceDetail } from '@/api/bpm'

const visible = ref(false)
const loading = ref(false)
const detail = ref(null)

async function open(processInstId) {
  visible.value = true
  loading.value = true
  detail.value = null
  try {
    const res = await getInstanceDetail(processInstId)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

const resultLabel = r => ({ 0: '进行中', 1: '已通过', 2: '已驳回', 3: '已撤回', 4: '已取消' }[r] || '未知')
const resultType = r => ({ 0: '', 1: 'success', 2: 'danger', 3: 'warning', 4: 'info' }[r] || '')

defineExpose({ open })
</script>
