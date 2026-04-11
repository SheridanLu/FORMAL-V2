<template>
  <div class="app-container">
    <page-header title="我的待办" />

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="task_name" label="任务节点" min-width="140" />
      <el-table-column prop="biz_type" label="业务类型" width="120" />
      <el-table-column prop="biz_no" label="业务单号" min-width="140" />
      <el-table-column prop="initiator_name" label="发起人" width="100" />
      <el-table-column prop="create_time" label="到达时间" width="160" />
      <el-table-column label="操作" width="260" align="center" fixed="right">
        <template #default="{ row }">
          <el-button v-if="!row.assignee_id" link type="primary" @click="handleClaim(row)">签收</el-button>
          <el-button link type="success" @click="openAction(row, 'complete')">通过</el-button>
          <el-button link type="danger" @click="openAction(row, 'reject')">驳回</el-button>
          <el-button link type="warning" @click="openAction(row, 'transfer')">转办</el-button>
          <el-button link @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0" :current-page="page" :page-size="size" :total="total"
      layout="total, prev, pager, next" @current-change="p => { page = p; fetchData() }"
      style="margin-top: 16px; justify-content: flex-end;"
    />

    <!-- 操作弹窗 -->
    <el-dialog v-model="actionVisible" :title="actionTitle" width="440px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="actionForm.comment" type="textarea" :rows="3" placeholder="请输入意见（可选）" />
        </el-form-item>
        <el-form-item v-if="actionType === 'transfer'" label="转办用户">
          <el-input v-model.number="actionForm.targetUserId" placeholder="请输入目标用户ID" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitAction">确定</el-button>
      </template>
    </el-dialog>

    <!-- 流程详情 -->
    <InstanceDetail ref="detailRef" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodoTasks, claimTask, completeTask, rejectTask, transferTask } from '@/api/bpm'
import InstanceDetail from '../instance/detail.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const actionVisible = ref(false)
const actionType = ref('')
const actionLoading = ref(false)
const currentTask = ref(null)
const actionForm = reactive({ comment: '', targetUserId: null })
const detailRef = ref(null)

const actionTitle = computed(() => ({
  complete: '通过审批',
  reject: '驳回',
  transfer: '转办'
}[actionType.value] || '操作'))

async function fetchData() {
  loading.value = true
  try {
    const res = await getTodoTasks({ page: page.value, size: size.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

async function handleClaim(row) {
  await claimTask(row.task_id)
  ElMessage.success('签收成功')
  fetchData()
}

function openAction(row, type) {
  currentTask.value = row
  actionType.value = type
  Object.assign(actionForm, { comment: '', targetUserId: null })
  actionVisible.value = true
}

async function submitAction() {
  actionLoading.value = true
  try {
    const taskId = currentTask.value.task_id
    if (actionType.value === 'complete') {
      await completeTask(taskId, actionForm)
      ElMessage.success('审批通过')
    } else if (actionType.value === 'reject') {
      await rejectTask(taskId, actionForm)
      ElMessage.success('已驳回')
    } else if (actionType.value === 'transfer') {
      await transferTask(taskId, actionForm)
      ElMessage.success('已转办')
    }
    actionVisible.value = false
    fetchData()
  } finally {
    actionLoading.value = false
  }
}

function viewDetail(row) {
  detailRef.value.open(row.process_inst_id)
}

onMounted(fetchData)
</script>
