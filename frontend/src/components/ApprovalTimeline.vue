<template>
  <el-timeline>
    <el-timeline-item
      v-for="(record, idx) in records"
      :key="idx"
      :type="getTimelineType(record.action)"
      :timestamp="record.created_at"
      placement="top"
    >
      <div class="timeline-content">
        <span class="operator">{{ record.operator_name }}</span>
        <el-tag :type="getTagType(record.action)" size="small" style="margin:0 8px">{{ getActionLabel(record.action) }}</el-tag>
        <span v-if="record.comment" class="comment">{{ record.comment }}</span>
      </div>
    </el-timeline-item>
  </el-timeline>
</template>

<script setup>
defineProps({
  records: { type: Array, default: () => [] },
  nodes: { type: Array, default: () => [] }
})

const ACTION_MAP = {
  submit:   { label: '提交',   tag: '', timeline: 'primary' },
  approve:  { label: '通过',   tag: 'success', timeline: 'success' },
  reject:   { label: '驳回',   tag: 'danger', timeline: 'danger' },
  cancel:   { label: '撤回',   tag: 'info', timeline: 'info' },
  delegate: { label: '转办',   tag: 'warning', timeline: 'warning' },
  read:     { label: '已阅',   tag: 'info', timeline: 'info' },
  add_sign: { label: '加签',   tag: 'warning', timeline: 'warning' }
}

function getActionLabel(action) { return ACTION_MAP[action]?.label ?? action }
function getTagType(action) { return ACTION_MAP[action]?.tag ?? 'info' }
function getTimelineType(action) { return ACTION_MAP[action]?.timeline ?? 'info' }
</script>

<style scoped>
.timeline-content { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; }
.operator { font-weight: 500; }
.comment { color: #606266; font-size: 13px; }
</style>
