<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button v-permission="'hr:entry-manage'" type="primary" @click="openCreate">新建入职</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="applicant_name" label="姓名" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="dept_id" label="部门ID" width="90" />
      <el-table-column prop="position" label="岗位" width="120" />
      <el-table-column prop="entry_date" label="入职日期" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-permission="'hr:entry-manage'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'hr:entry-manage'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑入职' : '新建入职'" width="650px" @closed="onDialogClosed">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="applicantName">
              <el-input v-model="form.applicantName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="部门ID" prop="deptId">
              <el-input-number v-model="form.deptId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="岗位">
              <el-input v-model="form.position" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker v-model="form.entryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="学历">
              <el-select v-model="form.education" clearable style="width: 100%">
                <el-option
                  v-for="opt in educationOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="工作年限">
              <el-input-number v-model="form.workYears" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证号">
              <el-input v-model="form.idCardNo" />
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
import { getEntryList, createEntry, updateEntry, deleteEntry } from '@/api/hr'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const defaultForm = {
  applicantName: '',
  phone: '',
  deptId: null,
  position: '',
  entryDate: null,
  education: '',
  workYears: null,
  idCardNo: ''
}

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getEntryList)
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createEntry, updateEntry, defaultForm)
const { options: educationOptions } = useDict('education_level')

const rules = {
  applicantName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  deptId: [{ required: true, message: '请输入部门ID', trigger: 'blur' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

function handleEdit(row) {
  openEdit({
    id: row.id,
    applicantName: row.applicant_name || '',
    phone: row.phone || '',
    deptId: row.dept_id,
    position: row.position || '',
    entryDate: row.entry_date || null,
    education: row.education || '',
    workYears: row.work_years,
    idCardNo: row.id_card_no || ''
  })
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteEntry(row.id)
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
