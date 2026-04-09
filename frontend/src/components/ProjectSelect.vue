<template>
  <el-select v-model="selected" :placeholder="placeholder" filterable clearable style="width:100%" @change="handleChange" v-bind="$attrs">
    <el-option v-for="p in options" :key="p.id" :label="p.project_name" :value="p.id" />
  </el-select>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getProjectList } from '@/api/project'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  placeholder: { type: String, default: '选择项目' }
})
const emit = defineEmits(['update:modelValue', 'change'])

const selected = ref(props.modelValue)
const options = ref([])

async function fetchOptions() {
  const res = await getProjectList({ page: 1, size: 999 })
  options.value = res.data?.records || []
}

function handleChange(val) {
  emit('update:modelValue', val)
  const item = options.value.find(o => o.id === val)
  emit('change', item)
}

watch(() => props.modelValue, (val) => { selected.value = val })
onMounted(fetchOptions)
</script>
