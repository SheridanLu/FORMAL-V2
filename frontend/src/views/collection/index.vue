<template>
  <div class="collection-page">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待回款" value="pending" />
            <el-option label="部分回款" value="partial" />
            <el-option label="已完成" value="completed" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 12px">
      <div style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center">
        <el-button type="primary" @click="handleAdd" v-permission="'finance:receipt'">新增收款计划</el-button>
        <el-tag v-if="overdueCount > 0" type="danger" size="large">逾期 {{ overdueCount }} 笔</el-tag>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="plan_no" label="期次" width="70" align="center" />
        <el-table-column prop="contract_id" label="合同ID" width="80" />
        <el-table-column prop="plan_amount" label="计划金额" width="130" align="right">
          <template #default="{ row }">{{ Number(row.plan_amount).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="plan_date" label="计划日期" width="110" />
        <el-table-column prop="actual_amount" label="实际金额" width="130" align="right">
          <template #default="{ row }">{{ row.actual_amount ? Number(row.actual_amount).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="actual_date" label="实际日期" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleFollowUp(row)">跟进</el-button>
            <el-button link type="success" size="small" v-if="row.status !== 'completed'" @click="handleConfirm(row)" v-permission="'finance:receipt'">确认回款</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'finance:receipt'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-if="total > 0" style="margin-top: 16px; justify-content: flex-end" background
        layout="total, sizes, prev, pager, next" :total="total" :page-sizes="[10, 20, 50]"
        v-model:current-page="queryForm.page" v-model:page-size="queryForm.size"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>

    <!-- 新增收款计划 -->
    <el-dialog v-model="planDialogVisible" title="新增收款计划" width="500px" destroy-on-close>
      <el-form ref="planFormRef" :model="planForm" :rules="planRules" label-width="100px">
        <el-form-item label="合同ID" prop="contractId">
          <el-input-number v-model="planForm.contractId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="期次" prop="planNo">
          <el-input-number v-model="planForm.planNo" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划金额" prop="planAmount">
          <el-input-number v-model="planForm.planAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划日期" prop="planDate">
          <el-date-picker v-model="planForm.planDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="planForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPlan">确定</el-button>
      </template>
    </el-dialog>

    <!-- 跟进记录弹窗 -->
    <el-dialog v-model="followUpVisible" title="跟进记录" width="700px" destroy-on-close>
      <div style="margin-bottom: 12px">
        <el-button type="primary" size="small" @click="showAddFollowUp = true">新增跟进</el-button>
      </div>
      <el-table :data="followUpList" stripe border size="small">
        <el-table-column prop="follow_up_date" label="跟进日期" width="110" />
        <el-table-column prop="result" label="跟进结果" min-width="200" />
        <el-table-column prop="next_action" label="下一步" min-width="150" />
      </el-table>

      <el-form v-if="showAddFollowUp" :model="followUpForm" label-width="80px" style="margin-top: 16px; border-top: 1px solid #eee; padding-top: 16px">
        <el-form-item label="跟进日期">
          <el-date-picker v-model="followUpForm.followUpDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="跟进结果">
          <el-input v-model="followUpForm.result" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="下一步">
          <el-input v-model="followUpForm.nextAction" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitFollowUp">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReceiptPlanList, createReceiptPlan, confirmReceipt, deleteReceiptPlan, getOverdueCount, getFollowUpList, createFollowUp } from '@/api/collection'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const overdueCount = ref(0)
const planDialogVisible = ref(false)
const followUpVisible = ref(false)
const showAddFollowUp = ref(false)
const followUpList = ref([])
const currentPlanId = ref(null)
const planFormRef = ref(null)

const statusMap = {
  pending: { text: '待回款', type: 'warning' },
  partial: { text: '部分回款', type: '' },
  completed: { text: '已完成', type: 'success' },
  overdue: { text: '已逾期', type: 'danger' }
}

const queryForm = reactive({ status: '', page: 1, size: 20 })
const planForm = reactive({ contractId: null, planNo: 1, planAmount: null, planDate: null, remark: '' })
const planRules = {
  contractId: [{ required: true, message: '请输入合同ID', trigger: 'blur' }],
  planNo: [{ required: true, message: '请输入期次', trigger: 'blur' }],
  planAmount: [{ required: true, message: '请输入计划金额', trigger: 'blur' }],
  planDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }]
}
const followUpForm = reactive({ receiptPlanId: null, followUpDate: null, result: '', nextAction: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getReceiptPlanList(queryForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const fetchOverdueCount = async () => {
  try {
    const res = await getOverdueCount()
    overdueCount.value = res.data?.count || 0
  } catch { /* ignore */ }
}

const handleSearch = () => { queryForm.page = 1; fetchData() }
const handleReset = () => { queryForm.status = ''; queryForm.page = 1; fetchData() }
const handleAdd = () => {
  Object.assign(planForm, { contractId: null, planNo: 1, planAmount: null, planDate: null, remark: '' })
  planDialogVisible.value = true
}

const submitPlan = async () => {
  await planFormRef.value.validate()
  submitting.value = true
  try {
    await createReceiptPlan(planForm)
    ElMessage.success('创建成功')
    planDialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const handleConfirm = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入实际回款金额', '确认回款', { inputPattern: /^\d+\.?\d*$/, inputErrorMessage: '请输入有效金额' })
  await confirmReceipt(row.id, value, null)
  ElMessage.success('已确认')
  fetchData(); fetchOverdueCount()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该收款计划？', '提示', { type: 'warning' })
  await deleteReceiptPlan(row.id)
  ElMessage.success('已删除')
  fetchData()
}

const handleFollowUp = async (row) => {
  currentPlanId.value = row.id
  followUpForm.receiptPlanId = row.id
  showAddFollowUp.value = false
  try {
    const res = await getFollowUpList(row.id)
    followUpList.value = res.data || []
  } catch { followUpList.value = [] }
  followUpVisible.value = true
}

const submitFollowUp = async () => {
  if (!followUpForm.result) { ElMessage.warning('请填写跟进结果'); return }
  await createFollowUp(followUpForm)
  ElMessage.success('已保存')
  showAddFollowUp.value = false
  const res = await getFollowUpList(currentPlanId.value)
  followUpList.value = res.data || []
}

onMounted(() => { fetchData(); fetchOverdueCount() })
</script>

<style scoped>
.search-card { margin-bottom: 0; }
</style>
