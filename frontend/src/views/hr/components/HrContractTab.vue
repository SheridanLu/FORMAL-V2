<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button v-permission="'hr:contract-manage'" type="primary" @click="openCreate">新建合同</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="user_id" label="员工ID" width="90" />
      <el-table-column prop="contract_type" label="合同类型" width="120">
        <template #default="{ row }">{{ getContractTypeLabel(row.contract_type) }}</template>
      </el-table-column>
      <el-table-column prop="start_date" label="开始日期" width="110" />
      <el-table-column prop="end_date" label="结束日期" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-permission="'hr:contract-manage'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'hr:contract-manage'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      style="margin-top: 16px; justify-content: flex-end"
      background
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.page"
      @current-change="handleCurrentChange"
    />

    <!-- Dialog -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑劳动合同' : '新建劳动合同'" width="600px" @closed="onDialogClosed">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="员工ID" prop="userId">
              <el-input-number v-model="form.userId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="form.contractType" style="width: 100%">
                <el-option
                  v-for="opt in contractTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getHrContractList, createHrContract, updateHrContract, deleteHrContract } from '@/api/hr'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const defaultForm = {
  userId: null,
  contractType: '',
  startDate: null,
  endDate: null
}

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getHrContractList)
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createHrContract, updateHrContract, defaultForm)
const { options: contractTypeOptions, getLabel: getContractTypeLabel } = useDict('hr_contract_type')

const rules = {
  userId: [{ required: true, message: '请输入员工ID', trigger: 'blur' }],
  contractType: [{ required: true, message: '请选择合同类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

function handleEdit(row) {
  openEdit({
    id: row.id,
    userId: row.user_id,
    contractType: row.contract_type || '',
    startDate: row.start_date || null,
    endDate: row.end_date || null
  })
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteHrContract(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

function onSubmit() {
  handleSubmit(fetchData)
}

function onDialogClosed() {
  Object.assign(form, { ...defaultForm })
  formRef.value?.resetFields()
}

onMounted(() => fetchData())
</script>
