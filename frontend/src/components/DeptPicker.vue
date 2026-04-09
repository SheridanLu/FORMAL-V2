<template>
  <el-dialog :model-value="visible" @update:model-value="$emit('update:visible', $event)" title="选择部门" width="500px" destroy-on-close>
    <el-input v-model="keyword" placeholder="搜索部门名称" clearable style="margin-bottom:12px" />
    <el-tree
      ref="treeRef"
      :data="deptTree"
      :props="{ label: 'dept_name', children: 'children' }"
      :filter-node-method="filterNode"
      node-key="id"
      highlight-current
      default-expand-all
      @node-click="handleSelect"
    />
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :disabled="!selected" @click="confirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getDeptTree } from '@/api/dept'

const props = defineProps({ visible: Boolean })
const emit = defineEmits(['update:visible', 'confirm'])

const keyword = ref('')
const treeRef = ref(null)
const deptTree = ref([])
const selected = ref(null)

async function fetchTree() {
  const res = await getDeptTree()
  deptTree.value = res.data || []
}

function filterNode(value, data) {
  if (!value) return true
  return data.dept_name?.includes(value)
}

function handleSelect(node) { selected.value = node }
function confirm() {
  if (selected.value) {
    emit('confirm', selected.value)
    emit('update:visible', false)
  }
}

watch(keyword, (val) => { treeRef.value?.filter(val) })
watch(() => props.visible, (val) => {
  if (val) {
    selected.value = null
    fetchTree()
  }
})
</script>
