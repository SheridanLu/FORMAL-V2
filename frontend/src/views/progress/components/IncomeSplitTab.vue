<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button type="primary" size="small" @click="dialogVisible = true" v-permission="'progress:report'">新增收入拆分</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="split_no" label="编号" width="130" />
      <el-table-column prop="split_item" label="拆分项" min-width="180" show-overflow-tooltip />
      <el-table-column prop="amount" label="金额" width="130" align="right">
        <template #default="{ row }">{{ row.amount ? Number(row.amount).toLocaleString() : '-' }}</template>
      </el-table-column>
      <el-table-column prop="ratio" label="比例" width="90" align="center">
        <template #default="{ row }">{{ row.ratio ? row.ratio + '%' : '-' }}</template>
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

    <el-dialog v-model="dialogVisible" title="新增收入拆分" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="拆分项" prop="splitItem">
          <el-input v-model="form.splitItem" placeholder="如: 土建工程、安装工程" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="比例(%)">
          <el-input-number v-model="form.ratio" :precision="2" :min="0" :max="100" controls-position="right" style="width: 100%" />
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
import { getIncomeSplitList, createIncomeSplit, submitIncomeSplit } from '@/api/progress'

const props = defineProps({ projectId: { type: Number, default: null } })

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({ projectId: null, splitItem: '', amount: null, ratio: null, remark: '' })
const rules = {
  splitItem: [{ required: true, message: '请输入拆分项', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 100 }
    if (props.projectId) params.projectId = props.projectId
    const res = await getIncomeSplitList(params)
    tableData.value = res.data?.records || res.data || []
  } finally { loading.value = false }
}

const handleCreate = async () => {
  await formRef.value.validate()
  submitting.value = true
  form.projectId = props.projectId
  try {
    await createIncomeSplit(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const handleSubmit = async (row) => {
  await submitIncomeSplit(row.id)
  ElMessage.success('已提交')
  fetchData()
}

watch(() => props.projectId, fetchData, { immediate: true })
</script>
