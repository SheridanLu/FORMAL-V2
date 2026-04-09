<template>
  <el-input-number
    :model-value="numericValue"
    @update:model-value="handleChange"
    :min="min"
    :max="max"
    :precision="precision"
    :step="1"
    controls-position="right"
    v-bind="$attrs"
  />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  min: { type: Number, default: 0 },
  max: { type: Number, default: undefined },
  precision: { type: Number, default: 4 }
})
const emit = defineEmits(['update:modelValue'])

const numericValue = computed(() => {
  const v = parseFloat(props.modelValue)
  return isNaN(v) ? undefined : v
})

function handleChange(val) {
  emit('update:modelValue', val === undefined || val === null ? '' : String(val))
}
</script>
