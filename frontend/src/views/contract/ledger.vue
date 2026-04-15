<template>
  <div class="contract-ledger-page">
    <!-- 汇总统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="合同总数" :value="stats.totalCount" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="收入合同金额" :value="stats.incomeAmount" :precision="2" prefix="¥" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="支出合同金额" :value="stats.expenseAmount" :precision="2" prefix="¥" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="即将到期" :value="stats.expiringCount">
            <template #suffix>
              <span style="font-size: 14px; color: #f56c6c">笔</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-form :model="queryForm" inline style="margin-bottom: 12px">
        <el-form-item label="合同类型">
          <el-select v-model="queryForm.contractType" placeholder="全部" clearable style="width: 140px">
            <el-option label="收入合同" value="income" />
            <el-option label="支出合同" value="expense" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="草稿" value="draft" />
            <el-option label="待审批" value="pending" />
            <el-option label="已审批" value="approved" />
            <el-option label="已完成" value="completed" />
            <el-option label="已终止" value="terminated" />
          </el-select>
        </el-form-item>
        <el-form-item label="签订日期">
          <el-date-picker v-model="queryForm.signDateRange" type="daterange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe border show-summary :summary-method="summaryMethod">
        <el-table-column prop="contract_no" label="合同编号" width="140" />
        <el-table-column prop="contract_name" label="合同名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contract_type" label="收/支" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.contract_type === 'income' ? 'success' : 'danger'" size="small">
              {{ row.contract_type === 'income' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="party_a" label="甲方" width="130" show-overflow-tooltip />
        <el-table-column prop="party_b" label="乙方" width="130" show-overflow-tooltip />
        <el-table-column prop="amount_with_tax" label="含税金额" width="130" align="right">
          <template #default="{ row }">{{ row.amount_with_tax ? Number(row.amount_with_tax).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="amount_without_tax" label="不含税金额" width="130" align="right">
          <template #default="{ row }">{{ row.amount_without_tax ? Number(row.amount_without_tax).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="tax_rate" label="税率%" width="70" align="center" />
        <el-table-column prop="sign_date" label="签订日期" width="110" />
        <el-table-column prop="start_date" label="开始日期" width="110" />
        <el-table-column prop="end_date" label="结束日期" width="110" />
        <el-table-column label="剩余天数" width="90" align="center">
          <template #default="{ row }">
            <template v-if="row.end_date">
              <el-tag v-if="remainDays(row.end_date) < 0" type="danger" size="small">已过期</el-tag>
              <el-tag v-else-if="remainDays(row.end_date) <= 30" type="warning" size="small">{{ remainDays(row.end_date) }}天</el-tag>
              <span v-else>{{ remainDays(row.end_date) }}天</span>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
              {{ statusMap[row.status]?.text || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/contracts/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-if="total > 0" style="margin-top: 16px; justify-content: flex-end" background
        layout="total, sizes, prev, pager, next" :total="total" :page-sizes="[20, 50, 100]"
        v-model:current-page="queryForm.page" v-model:page-size="queryForm.size"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getContractList } from '@/api/contract'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const statusMap = {
  draft: { text: '草稿', type: 'info' },
  pending: { text: '待审批', type: 'warning' },
  approved: { text: '已审批', type: 'success' },
  rejected: { text: '已驳回', type: 'danger' },
  terminated: { text: '已终止', type: 'info' },
  closed: { text: '已关闭', type: 'info' },
  completed: { text: '已完成', type: 'success' }
}

const queryForm = reactive({
  contractType: '', status: '', signDateRange: null, page: 1, size: 50
})

// 汇总统计
const stats = reactive({ totalCount: 0, incomeAmount: 0, expenseAmount: 0, expiringCount: 0 })

const remainDays = (endDate) => {
  if (!endDate) return Infinity
  const diff = new Date(endDate) - new Date()
  return Math.ceil(diff / 86400000)
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryForm }
    if (queryForm.signDateRange?.length === 2) {
      params.signDateStart = queryForm.signDateRange[0]
      params.signDateEnd = queryForm.signDateRange[1]
    }
    delete params.signDateRange
    const res = await getContractList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
    calcStats()
  } finally { loading.value = false }
}

const calcStats = () => {
  const all = tableData.value
  stats.totalCount = total.value
  stats.incomeAmount = all.filter(c => c.contract_type === 'income')
    .reduce((s, c) => s + (Number(c.amount_with_tax) || 0), 0)
  stats.expenseAmount = all.filter(c => c.contract_type === 'expense')
    .reduce((s, c) => s + (Number(c.amount_with_tax) || 0), 0)
  stats.expiringCount = all.filter(c => {
    const d = remainDays(c.end_date)
    return d >= 0 && d <= 30
  }).length
}

const summaryMethod = ({ columns, data }) => {
  return columns.map((col, idx) => {
    if (idx === 0) return '合计'
    if (col.property === 'amount_with_tax') {
      return data.reduce((s, r) => s + (Number(r.amount_with_tax) || 0), 0).toLocaleString()
    }
    if (col.property === 'amount_without_tax') {
      return data.reduce((s, r) => s + (Number(r.amount_without_tax) || 0), 0).toLocaleString()
    }
    return ''
  })
}

const handleSearch = () => { queryForm.page = 1; fetchData() }
const handleReset = () => {
  queryForm.contractType = ''; queryForm.status = ''; queryForm.signDateRange = null
  queryForm.page = 1; fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.contract-ledger-page :deep(.el-statistic__head) { font-size: 13px; }
.contract-ledger-page :deep(.el-statistic__content) { font-size: 22px; }
</style>
