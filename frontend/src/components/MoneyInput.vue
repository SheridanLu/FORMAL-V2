<template>
  <el-input
    :model-value="displayValue"
    @input="handleInput"
    @blur="handleBlur"
    v-bind="$attrs"
    placeholder="0.00"
  >
    <template #prefix>¥</template>
  </el-input>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' }
})
const emit = defineEmits(['update:modelValue'])

const displayValue = computed(() => {
  if (props.modelValue === '' || props.modelValue === null || props.modelValue === undefined) return ''
  return String(props.modelValue)
})

function handleInput(val) {
  // 只允许数字和小数点，最多2位小数
  const cleaned = val.replace(/[^\d.]/g, '').replace(/(\..*)\./g, '$1')
  const parts = cleaned.split('.')
  if (parts[1]?.length > 2) parts[1] = parts[1].slice(0, 2)
  emit('update:modelValue', parts.join('.'))
}

function handleBlur() {
  if (props.modelValue !== '' && props.modelValue !== null) {
    const num = parseFloat(props.modelValue)
    if (!isNaN(num)) {
      emit('update:modelValue', num.toFixed(2))
    }
  }
}
</script>
