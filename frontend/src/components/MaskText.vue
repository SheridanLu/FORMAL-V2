<template>
  <span class="mask-text">
    <span>{{ displayValue }}</span>
    <el-button v-if="canViewRaw && value" type="primary" link size="small"
               @click="visible = !visible" style="margin-left: 4px">
      <el-icon><component :is="visible ? 'Hide' : 'View'" /></el-icon>
    </el-button>
  </span>
</template>

<script setup>
import { ref, computed } from 'vue'
import { mask } from '@/utils/desensitize'
import { usePermission } from '@/composables/usePermission'

const props = defineProps({
  value: { type: String, default: '' },
  type: { type: String, default: 'phone' } // phone/idcard/bank/email
})

const { hasAnyPermission } = usePermission()
const visible = ref(false)

// GM 或 HR 角色可查看原文
const canViewRaw = computed(() => {
  return hasAnyPermission(['system:user-manage'])
})

const displayValue = computed(() => {
  if (!props.value) return '-'
  if (visible.value) return props.value
  return mask(props.value, props.type)
})
</script>

<style scoped>
.mask-text { display: inline-flex; align-items: center; }
</style>
