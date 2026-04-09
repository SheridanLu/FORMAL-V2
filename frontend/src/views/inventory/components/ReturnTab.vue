<template>
  <div>
    <div class="toolbar">
      <project-select v-model="query.projectId" placeholder="按项目筛选" clearable style="width: 200px; margin-right: 8px" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button v-permission="'inventory:return'" type="primary" @click="openCreate" style="margin-left: auto">新建退货单</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="return_no" label="退货单号" width="140" />
      <el-table-column prop="project_name" label="关联项目" min-width="120" />
      <el-table-column prop="dispose_method" label="处理方式" width="120" />
      <el-table-column prop="return_date" label="退货日期" width="110" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-permission="'inventory:return'" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'draft'" v-permission="'inventory:return'" type="success" link size="small" @click="handleSubmitReturn(row)">提交</el-button>
          <el-button v-permission="'inventory:return'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑退货单' : '新建退货单'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="关联项目" prop="projectId"><project-select v-model="form.projectId" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处置方式" prop="disposeMethod">
              <el-select v-model="form.disposeMethod" style="width: 100%">
                <el-option label="退回供应商" value="退回供应商" />
                <el-option label="项目调拨" value="项目调拨" />
                <el-option label="报废处置" value="报废处置" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="目标项目">
              <project-select v-model="form.targetProjectId" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退货日期">
              <el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit(onSuccess)">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getReturnList, createReturn, updateReturn, deleteReturn, submitReturn } from '@/api/inventory'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'

const { loading, tableData, total, query, fetchData, handleSearch, handleReset, handleSizeChange, handleCurrentChange } = useTable(getReturnList, { projectId: null })
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createReturn, updateReturn, {
  projectId: null, disposeMethod: '', targetProjectId: null, returnDate: '', remark: ''
})

const rules = {
  projectId: [{ required: true, message: '请选择关联项目', trigger: 'change' }],
  disposeMethod: [{ required: true, message: '请选择处置方式', trigger: 'change' }]
}

const onSuccess = () => fetchData()

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该退货单？', '提示', { type: 'warning' }).then(async () => {
    await deleteReturn(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmitReturn = (row) => {
  ElMessageBox.confirm('确认提交该退货单？提交后将进入审批流程。', '提示', { type: 'warning' }).then(async () => {
    await submitReturn(row.id)
    ElMessage.success('提交成功')
    fetchData()
  }).catch(() => {})
}

fetchData()
</script>
