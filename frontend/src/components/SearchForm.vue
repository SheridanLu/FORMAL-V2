<template>
  <el-form :model="modelValue" inline class="search-wrapper" @submit.prevent="$emit('search')">
    <slot />
    <el-form-item>
      <el-button type="primary" @click="$emit('search')">查询</el-button>
      <el-button @click="$emit('reset')">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
defineProps({
  modelValue: { type: Object, default: () => ({}) }
})
defineEmits(['search', 'reset'])
</script>

<style scoped>
.search-wrapper { padding-bottom: 12px; }
</style>
