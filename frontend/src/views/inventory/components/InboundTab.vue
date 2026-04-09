<template>
  <div>
    <div class="toolbar">
      <project-select v-model="query.projectId" placeholder="按项目筛选" clearable style="width: 200px; margin-right: 8px" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button v-permission="'inventory:inbound'" type="primary" @click="openCreate" style="margin-left: auto">新建入库单</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="inbound_no" label="入库单号" width="140" />
      <el-table-column prop="project_name" label="关联项目" min-width="120" />
      <el-table-column prop="contract_id" label="合同ID" width="100" />
      <el-table-column prop="warehouse" label="仓库" width="120" />
      <el-table-column prop="inbound_date" label="入库日期" width="110" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-permission="'inventory:inbound'" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'draft'" v-permission="'inventory:inbound'" type="success" link size="small" @click="handleSubmitInbound(row)">提交</el-button>
          <el-button v-permission="'inventory:inbound'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑入库单' : '新建入库单'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="关联项目" prop="projectId"><project-select v-model="form.projectId" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联合同" prop="contractId">
              <el-input-number v-model="form.contractId" :min="1" controls-position="right" style="width: 100%" placeholder="合同ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="仓库"><el-input v-model="form.warehouse" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库日期">
              <el-date-picker v-model="form.inboundDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { getInboundList, createInbound, updateInbound, deleteInbound, submitInbound } from '@/api/inventory'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'

const { loading, tableData, total, query, fetchData, handleSearch, handleReset, handleSizeChange, handleCurrentChange } = useTable(getInboundList, { projectId: null })
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createInbound, updateInbound, {
  projectId: null, contractId: null, warehouse: '', inboundDate: '', remark: ''
})

const rules = {
  projectId: [{ required: true, message: '请选择关联项目', trigger: 'change' }],
  contractId: [{ required: true, message: '请输入关联合同', trigger: 'blur' }]
}

const onSuccess = () => fetchData()

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该入库单？', '提示', { type: 'warning' }).then(async () => {
    await deleteInbound(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmitInbound = (row) => {
  ElMessageBox.confirm('确认提交该入库单？提交后将进入审批流程。', '提示', { type: 'warning' }).then(async () => {
    await submitInbound(row.id)
    ElMessage.success('提交成功')
    fetchData()
  }).catch(() => {})
}

fetchData()
</script>
