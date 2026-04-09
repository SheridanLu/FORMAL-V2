<template>
  <el-select v-model="selected" :placeholder="placeholder" filterable clearable style="width:100%" @change="handleChange" v-bind="$attrs">
    <el-option v-for="c in options" :key="c.id" :label="c.contract_name" :value="c.id">
      <span>{{ c.contract_name }}</span>
      <span style="float:right;color:#909399;font-size:12px">{{ c.contract_no }}</span>
    </el-option>
  </el-select>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getContractList } from '@/api/contract'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  placeholder: { type: String, default: '选择合同' },
  contractType: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue', 'change'])

const selected = ref(props.modelValue)
const options = ref([])

async function fetchOptions() {
  const params = { page: 1, size: 999 }
  if (props.contractType) params.contract_type = props.contractType
  const res = await getContractList(params)
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
