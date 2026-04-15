<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button type="primary" size="small" @click="dialogVisible = true" v-permission="'progress:report'">新增产值报表</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="statement_no" label="编号" width="130" />
      <el-table-column prop="period" label="期次" width="100" />
      <el-table-column prop="planned_amount" label="计划产值" width="130" align="right">
        <template #default="{ row }">{{ row.planned_amount ? Number(row.planned_amount).toLocaleString() : '-' }}</template>
      </el-table-column>
      <el-table-column prop="actual_amount" label="实际产值" width="130" align="right">
        <template #default="{ row }">{{ row.actual_amount ? Number(row.actual_amount).toLocaleString() : '-' }}</template>
      </el-table-column>
      <el-table-column prop="completion_rate" label="完成率" width="90" align="center">
        <template #default="{ row }">{{ row.completion_rate ? row.completion_rate + '%' : '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'submitted' ? 'success' : 'info'" size="small">
            {{ row.status === 'submitted' ? '已提交' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 'draft'" link type="primary" size="small" @click="handleSubmit(row)" v-permission="'progress:report'">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增产值报表" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="期次" prop="period">
          <el-input v-model="form.period" placeholder="如: 2026-04" />
        </el-form-item>
        <el-form-item label="计划产值" prop="plannedAmount">
          <el-input-number v-model="form.plannedAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际产值" prop="actualAmount">
          <el-input-number v-model="form.actualAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getStatementList, createStatement, submitStatement } from '@/api/progress'

const props = defineProps({ projectId: { type: Number, default: null } })

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({ projectId: null, period: '', plannedAmount: null, actualAmount: null, remark: '' })
const rules = {
  period: [{ required: true, message: '请输入期次', trigger: 'blur' }],
  plannedAmount: [{ required: true, message: '请输入计划产值', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 100 }
    if (props.projectId) params.projectId = props.projectId
    const res = await getStatementList(params)
    tableData.value = res.data?.records || res.data || []
  } finally { loading.value = false }
}

const handleCreate = async () => {
  await formRef.value.validate()
  submitting.value = true
  form.projectId = props.projectId
  try {
    await createStatement(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const handleSubmit = async (row) => {
  await submitStatement(row.id)
  ElMessage.success('已提交')
  fetchData()
}

watch(() => props.projectId, fetchData, { immediate: true })
</script>
