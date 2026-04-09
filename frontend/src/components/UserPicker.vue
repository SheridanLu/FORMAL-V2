<template>
  <el-dialog :model-value="visible" @update:model-value="$emit('update:visible', $event)" title="选择人员" width="600px" destroy-on-close>
    <el-input v-model="keyword" placeholder="搜索姓名/工号" clearable style="margin-bottom:12px" @keyup.enter="doSearch" />
    <el-table v-loading="loading" :data="userList" border highlight-current-row @current-change="handleSelect" max-height="400">
      <el-table-column prop="real_name" label="姓名" width="100" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="dept_name" label="部门" />
      <el-table-column prop="phone" label="手机" width="130" />
    </el-table>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :disabled="!selected" @click="confirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getUserList } from '@/api/user'

const props = defineProps({ visible: Boolean })
const emit = defineEmits(['update:visible', 'confirm'])

const keyword = ref('')
const loading = ref(false)
const userList = ref([])
const selected = ref(null)

async function doSearch() {
  loading.value = true
  try {
    const res = await getUserList({ page: 1, size: 50, keyword: keyword.value })
    userList.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

function handleSelect(row) { selected.value = row }
function confirm() {
  if (selected.value) {
    emit('confirm', selected.value)
    emit('update:visible', false)
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    keyword.value = ''
    selected.value = null
    doSearch()
  }
})
</script>
