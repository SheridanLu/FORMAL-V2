<template>
  <el-select v-model="selected" :placeholder="placeholder" filterable clearable style="width:100%" @change="handleChange" v-bind="$attrs">
    <el-option v-for="m in options" :key="m.id" :label="m.material_name" :value="m.id">
      <span>{{ m.material_name }}</span>
      <span style="float:right;color:#909399;font-size:12px">{{ m.spec }} / {{ m.unit }}</span>
    </el-option>
  </el-select>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getAllMaterials } from '@/api/material'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  placeholder: { type: String, default: '选择物料' }
})
const emit = defineEmits(['update:modelValue', 'change'])

const selected = ref(props.modelValue)
const options = ref([])

async function fetchOptions() {
  const res = await getAllMaterials()
  options.value = res.data || []
}

function handleChange(val) {
  emit('update:modelValue', val)
  const item = options.value.find(o => o.id === val)
  emit('change', item)
}

watch(() => props.modelValue, (val) => { selected.value = val })
onMounted(fetchOptions)
</script>
