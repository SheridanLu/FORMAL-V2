<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button v-permission="'hr:resign-manage'" type="primary" @click="openCreate">新建离职</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="user_id" label="员工ID" width="90" />
      <el-table-column prop="resign_type" label="离职类型" width="100">
        <template #default="{ row }">{{ getResignTypeLabel(row.resign_type) }}</template>
      </el-table-column>
      <el-table-column prop="resign_date" label="离职日期" width="110" />
      <el-table-column prop="resign_reason" label="离职原因" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-permission="'hr:resign-manage'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'hr:resign-manage'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑离职' : '新建离职'" width="550px" @closed="onDialogClosed">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="员工ID" prop="userId">
              <el-input-number v-model="form.userId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="离职类型" prop="resignType">
              <el-select v-model="form.resignType" style="width: 100%">
                <el-option
                  v-for="opt in resignTypeOptions"
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
            <el-form-item label="离职日期" prop="resignDate">
              <el-date-picker v-model="form.resignDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交接人ID">
              <el-input-number v-model="form.handoverTo" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="离职原因">
          <el-input v-model="form.resignReason" type="textarea" :rows="2" />
        </el-form-item>
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
import { getResignList, createResign, updateResign, deleteResign } from '@/api/hr'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const defaultForm = {
  userId: null,
  resignType: '',
  resignDate: null,
  resignReason: '',
  handoverTo: null
}

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getResignList)
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createResign, updateResign, defaultForm)
const { options: resignTypeOptions, getLabel: getResignTypeLabel } = useDict('resign_type')

const rules = {
  userId: [{ required: true, message: '请输入员工ID', trigger: 'blur' }],
  resignType: [{ required: true, message: '请选择离职类型', trigger: 'change' }],
  resignDate: [{ required: true, message: '请选择离职日期', trigger: 'change' }]
}

function handleEdit(row) {
  openEdit({
    id: row.id,
    userId: row.user_id,
    resignType: row.resign_type || '',
    resignDate: row.resign_date || null,
    resignReason: row.resign_reason || '',
    handoverTo: row.handover_to
  })
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteResign(row.id)
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
