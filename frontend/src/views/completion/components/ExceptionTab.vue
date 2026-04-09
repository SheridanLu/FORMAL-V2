<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button type="primary" v-permission="'completion:finish-manage'" @click="openCreate">新建异常工单</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="biz_type" label="业务类型" width="120">
        <template #default="{ row }">{{ bizTypeDict.getLabel(row.biz_type) }}</template>
      </el-table-column>
      <el-table-column prop="biz_id" label="业务ID" width="90" />
      <el-table-column prop="fail_reason" label="失败原因" min-width="250" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'warning' : 'success'" size="small">
            {{ row.status === 1 ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" link size="small" v-permission="'completion:finish-manage'" @click="handleDelete(row)">删除</el-button>
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

    <!-- Dialog (create only, no update) -->
    <el-dialog v-model="formVisible" title="新建异常工单" width="550px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="业务类型" prop="bizType">
              <el-select v-model="form.bizType" style="width: 100%">
                <el-option v-for="opt in bizTypeDict.options.value" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务单据ID" prop="bizId">
              <el-input-number v-model="form.bizId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="失败原因" prop="failReason">
          <el-input v-model="form.failReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="处理人ID">
          <el-input-number v-model="form.handlerId" :min="1" controls-position="right" style="width: 220px" />
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
import { getExceptionList, createException, deleteException } from '@/api/completion'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getExceptionList)

const bizTypeDict = useDict('exception_biz_type')

const defaultForm = {
  bizType: '',
  bizId: null,
  failReason: '',
  handlerId: null
}

const { formRef, submitting, formVisible, form, openCreate, handleSubmit } =
  useForm(createException, null, defaultForm)

const rules = {
  bizType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  bizId: [{ required: true, message: '请输入业务单据ID', trigger: 'blur' }],
  failReason: [{ required: true, message: '请输入失败原因', trigger: 'blur' }]
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteException(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
