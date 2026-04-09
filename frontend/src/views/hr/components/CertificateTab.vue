<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button v-permission="'hr:certificate-manage'" type="primary" @click="openCreate">新建证书</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="user_id" label="员工ID" width="90" />
      <el-table-column prop="cert_name" label="证书名称" min-width="150" />
      <el-table-column prop="cert_type" label="证书类型" width="120">
        <template #default="{ row }">{{ getCertTypeLabel(row.cert_type) }}</template>
      </el-table-column>
      <el-table-column prop="cert_no" label="证书编号" width="140" />
      <el-table-column prop="expire_date" label="到期日期" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-permission="'hr:certificate-manage'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'hr:certificate-manage'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑证书' : '新建证书'" width="650px" @closed="onDialogClosed">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="证书名称" prop="certName">
              <el-input v-model="form.certName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书类型" prop="certType">
              <el-select v-model="form.certType" style="width: 100%">
                <el-option
                  v-for="opt in certTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="员工ID">
              <el-input-number v-model="form.userId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="证书编号">
              <el-input v-model="form.certNo" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="证书分类">
              <el-input v-model="form.certCategory" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发证日期">
              <el-date-picker v-model="form.issueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期">
              <el-date-picker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { getCertificateList, createCertificate, updateCertificate, deleteCertificate } from '@/api/hr'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const defaultForm = {
  certType: '',
  userId: null,
  certName: '',
  certCategory: '',
  certNo: '',
  issueDate: null,
  expireDate: null
}

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getCertificateList)
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createCertificate, updateCertificate, defaultForm)
const { options: certTypeOptions, getLabel: getCertTypeLabel } = useDict('cert_type')

const rules = {
  certName: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
  certType: [{ required: true, message: '请选择证书类型', trigger: 'change' }]
}

function handleEdit(row) {
  openEdit({
    id: row.id,
    certType: row.cert_type || '',
    userId: row.user_id,
    certName: row.cert_name || '',
    certCategory: row.cert_category || '',
    certNo: row.cert_no || '',
    issueDate: row.issue_date || null,
    expireDate: row.expire_date || null
  })
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCertificate(row.id)
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
