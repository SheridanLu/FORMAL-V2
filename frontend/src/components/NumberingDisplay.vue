<template>
  <span class="numbering-display">{{ value || '-' }}</span>
</template>

<script setup>
defineProps({
  value: { type: String, default: '' }
})
</script>

<style scoped>
.numbering-display {
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #409eff;
  letter-spacing: 0.5px;
}
</style>
