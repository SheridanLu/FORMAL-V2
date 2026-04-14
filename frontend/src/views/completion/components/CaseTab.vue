<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button type="primary" v-permission="['progress:report','project:view-all']" @click="openCreate">新建案例</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="case_name" label="案件名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="case_type" label="案件类型" width="120">
        <template #default="{ row }">{{ caseTypeDict.getLabel(row.case_type) }}</template>
      </el-table-column>
      <el-table-column prop="project_id" label="关联项目" width="90" />
      <el-table-column prop="summary" label="案件摘要" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" link size="small" v-permission="['progress:report','project:view-all']" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" v-permission="['progress:report','project:view-all']" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑案例' : '新建案例'" width="650px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="案例名称" prop="caseName">
              <el-input v-model="form.caseName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案例类型">
              <el-select v-model="form.caseType" clearable style="width: 100%">
                <el-option v-for="opt in caseTypeDict.options.value" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联项目">
          <project-select v-model="form.projectId" clearable />
        </el-form-item>
        <el-form-item label="案例摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="案例内容">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit(fetchData)">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getCaseList, createCase, updateCase, deleteCase } from '@/api/completion'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getCaseList)

const caseTypeDict = useDict('case_type')

const defaultForm = {
  projectId: null,
  caseName: '',
  caseType: '',
  summary: '',
  content: ''
}

const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } =
  useForm(createCase, updateCase, defaultForm)

const rules = {
  caseName: [{ required: true, message: '请输入案例名称', trigger: 'blur' }]
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCase(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
