<template>
  <div class="page-header">
    <span class="page-title"><slot name="title">{{ title }}</slot></span>
    <div class="page-actions">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.page-actions {
  display: flex;
  gap: 8px;
}
</style>
