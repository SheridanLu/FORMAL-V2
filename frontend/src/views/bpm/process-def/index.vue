<template>
  <div class="app-container">
    <search-form>
      <el-form inline>
        <el-form-item label="流程名称">
          <el-input v-model="keyword" placeholder="请输入" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
      </el-form>
    </search-form>

    <page-header title="流程定义管理">
      <el-button v-permission="'bpm:process-manage'" type="primary" @click="openDesigner()">部署新流程</el-button>
    </page-header>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="key" label="流程Key" min-width="180" />
      <el-table-column prop="name" label="流程名称" min-width="160" />
      <el-table-column prop="version" label="版本" width="70" align="center" />
      <el-table-column prop="biz_type" label="业务类型" width="120" />
      <el-table-column prop="suspended" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.suspended ? 'danger' : 'success'" size="small">
            {{ row.suspended ? '已挂起' : '运行中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'bpm:process-manage'" link type="primary" @click="openDesigner(row)">查看</el-button>
          <el-button v-permission="'bpm:process-manage'" link :type="row.suspended ? 'success' : 'warning'"
            @click="toggleSuspend(row)">
            {{ row.suspended ? '激活' : '挂起' }}
          </el-button>
          <el-button v-permission="'bpm:process-manage'" link type="primary" @click="openExtEdit(row)">配置</el-button>
          <el-button v-permission="'bpm:process-manage'" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- BPMN 设计器弹窗 -->
    <BpmnDesigner ref="designerRef" @deployed="fetchData" />

    <!-- 扩展配置弹窗 -->
    <el-dialog v-model="extVisible" title="流程扩展配置" width="500px" destroy-on-close>
      <el-form :model="extForm" label-width="110px">
        <el-form-item label="关联业务类型">
          <el-input v-model="extForm.bizType" placeholder="如 contract_approval" />
        </el-form-item>
        <el-form-item label="候选人策略">
          <el-radio-group v-model="extForm.candidateStrategy">
            <el-radio :value="1">指定用户</el-radio>
            <el-radio :value="2">指定角色</el-radio>
            <el-radio :value="3">部门主管</el-radio>
            <el-radio :value="4">发起人自选</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="候选参数">
          <el-input v-model="extForm.candidateParam" placeholder="用户ID或角色code，逗号分隔" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="extForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="extVisible = false">取消</el-button>
        <el-button type="primary" :loading="extSaving" @click="saveExtConfig">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProcessDefList, suspendProcess, activateProcess, deleteProcessDef, updateProcessExt } from '@/api/bpm'
import BpmnDesigner from './designer.vue'

const keyword = ref('')
const loading = ref(false)
const tableData = ref([])
const designerRef = ref(null)
const extVisible = ref(false)
const extSaving = ref(false)
const currentDefId = ref('')
const extForm = reactive({ bizType: '', candidateStrategy: 1, candidateParam: '', remark: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getProcessDefList({ keyword: keyword.value })
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openDesigner(row) {
  designerRef.value.open(row)
}

async function toggleSuspend(row) {
  if (row.suspended) {
    await activateProcess(row.id)
    ElMessage.success('已激活')
  } else {
    await suspendProcess(row.id)
    ElMessage.success('已挂起')
  }
  fetchData()
}

function openExtEdit(row) {
  currentDefId.value = row.id
  Object.assign(extForm, {
    bizType: row.biz_type || '',
    candidateStrategy: row.candidate_strategy || 1,
    candidateParam: row.candidate_param || '',
    remark: row.remark || ''
  })
  extVisible.value = true
}

async function saveExtConfig() {
  extSaving.value = true
  try {
    await updateProcessExt(currentDefId.value, extForm)
    ElMessage.success('配置已保存')
    extVisible.value = false
    fetchData()
  } finally {
    extSaving.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除流程定义「${row.name || row.key}」？运行中的实例也会被删除。`, '提示', { type: 'warning' })
  await deleteProcessDef(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>
