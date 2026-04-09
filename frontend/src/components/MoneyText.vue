<template>
  <span :class="['money-text', { negative: isNegative }]">¥{{ formatted }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  value: { type: [String, Number], default: 0 }
})

const numVal = computed(() => {
  const n = parseFloat(props.value)
  return isNaN(n) ? 0 : n
})

const isNegative = computed(() => numVal.value < 0)

const formatted = computed(() => {
  const abs = Math.abs(numVal.value)
  const str = abs.toFixed(2)
  const [intPart, decPart] = str.split('.')
  const withCommas = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  return (isNegative.value ? '-' : '') + withCommas + '.' + decPart
})
</script>

<style scoped>
.money-text { white-space: nowrap; }
.money-text.negative { color: #f56c6c; }
</style>
