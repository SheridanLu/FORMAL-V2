<template>
  <div class="cost-dashboard-page">
    <!-- 项目选择 -->
    <el-card shadow="never" style="margin-bottom: 12px">
      <el-form inline>
        <el-form-item label="项目">
          <el-select v-model="projectId" placeholder="选择项目" filterable clearable style="width: 300px" @change="fetchAll">
            <el-option v-for="p in projects" :key="p.id" :label="p.project_name || p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预算 vs 实际 汇总 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="合同金额(收入)" :value="overview.incomeAmount" :precision="2" prefix="¥" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="已付款总额" :value="overview.paidAmount" :precision="2" prefix="¥" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="已回款总额" :value="overview.receivedAmount" :precision="2" prefix="¥" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <el-statistic title="利润率" :value="overview.profitRate" :precision="1" suffix="%" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 成本明细 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>成本归集明细</span>
        </div>
      </template>

      <el-table :data="costData" v-loading="loading" stripe border show-summary :summary-method="summaryMethod">
        <el-table-column prop="category" label="成本类别" width="140" />
        <el-table-column prop="budget_amount" label="预算金额" width="130" align="right">
          <template #default="{ row }">{{ fmt(row.budget_amount) }}</template>
        </el-table-column>
        <el-table-column prop="actual_amount" label="实际金额" width="130" align="right">
          <template #default="{ row }">{{ fmt(row.actual_amount) }}</template>
        </el-table-column>
        <el-table-column label="偏差" width="130" align="right">
          <template #default="{ row }">
            <span :style="{ color: deviation(row) < 0 ? '#67c23a' : deviation(row) > 0 ? '#f56c6c' : '' }">
              {{ fmt(deviation(row)) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="执行率" width="100" align="center">
          <template #default="{ row }">
            <el-progress :percentage="execRate(row)" :stroke-width="6" :status="execRate(row) > 100 ? 'exception' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-card>

    <!-- 费用趋势(简化版) -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header><span>近6个月付款趋势</span></template>
      <div v-if="trendData.length" style="display: flex; align-items: flex-end; gap: 8px; height: 200px; padding: 16px 0">
        <div v-for="item in trendData" :key="item.month" style="flex: 1; text-align: center">
          <div class="trend-bar" :style="{ height: trendBarHeight(item.amount) + 'px' }">
            <span class="trend-value">{{ (item.amount / 10000).toFixed(1) }}万</span>
          </div>
          <div style="font-size: 12px; color: #909399; margin-top: 4px">{{ item.month }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getCostLedgerList, getCostSummary, getPaymentList } from '@/api/finance'
import { getReceiptPlanList } from '@/api/collection'
import { getContractList } from '@/api/contract'
import { getAllProjects } from '@/api/project'

const loading = ref(false)
const projectId = ref(null)
const projects = ref([])
const costData = ref([])
const trendData = ref([])

const overview = reactive({
  incomeAmount: 0,
  paidAmount: 0,
  receivedAmount: 0,
  profitRate: 0
})

const fmt = (v) => v != null ? Number(v).toLocaleString(undefined, { minimumFractionDigits: 2 }) : '-'

const deviation = (row) => {
  const b = Number(row.budget_amount) || 0
  const a = Number(row.actual_amount) || 0
  return a - b
}

const execRate = (row) => {
  const b = Number(row.budget_amount) || 0
  if (b === 0) return 0
  return Math.round((Number(row.actual_amount) || 0) / b * 100)
}

const trendBarHeight = (amount) => {
  const max = Math.max(...trendData.value.map(d => d.amount), 1)
  return Math.max(4, (amount / max) * 160)
}

const summaryMethod = ({ columns, data }) => {
  return columns.map((col, idx) => {
    if (idx === 0) return '合计'
    if (col.property === 'budget_amount') {
      return fmt(data.reduce((s, r) => s + (Number(r.budget_amount) || 0), 0))
    }
    if (col.property === 'actual_amount') {
      return fmt(data.reduce((s, r) => s + (Number(r.actual_amount) || 0), 0))
    }
    return ''
  })
}

const fetchAll = async () => {
  if (!projectId.value) return
  loading.value = true
  try {
    // 获取成本明细
    const costRes = await getCostLedgerList({ projectId: projectId.value, size: 100 })
    costData.value = costRes.data?.records || costRes.data || []

    // 获取费用汇总
    try {
      const summaryRes = await getCostSummary({ projectId: projectId.value })
      const s = summaryRes.data || {}
      overview.paidAmount = Number(s.paid_amount || s.paidAmount || 0)
    } catch { /* ignore */ }

    // 获取收入合同金额
    try {
      const contractRes = await getContractList({ projectId: projectId.value, contractType: 'income', size: 100 })
      const contracts = contractRes.data?.records || []
      overview.incomeAmount = contracts.reduce((s, c) => s + (Number(c.amount_with_tax) || 0), 0)
    } catch { /* ignore */ }

    // 获取回款总额
    try {
      const receiptRes = await getReceiptPlanList({ projectId: projectId.value, status: 'completed', size: 100 })
      const plans = receiptRes.data?.records || []
      overview.receivedAmount = plans.reduce((s, p) => s + (Number(p.actual_amount) || 0), 0)
    } catch { /* ignore */ }

    // 计算利润率
    if (overview.incomeAmount > 0) {
      overview.profitRate = ((overview.incomeAmount - overview.paidAmount) / overview.incomeAmount) * 100
    } else {
      overview.profitRate = 0
    }

    // 生成趋势数据 (从付款记录)
    try {
      const payRes = await getPaymentList({ projectId: projectId.value, size: 200 })
      const payments = payRes.data?.records || payRes.data || []
      const monthMap = {}
      payments.forEach(p => {
        const month = (p.payment_date || p.created_at || '').substring(0, 7)
        if (month) {
          monthMap[month] = (monthMap[month] || 0) + (Number(p.amount) || Number(p.payment_amount) || 0)
        }
      })
      // 取最近6个月
      const months = Object.keys(monthMap).sort().slice(-6)
      trendData.value = months.map(m => ({ month: m, amount: monthMap[m] }))
    } catch { trendData.value = [] }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getAllProjects()
    projects.value = res.data || []
  } catch { /* ignore */ }
})
</script>

<style scoped>
.cost-dashboard-page :deep(.el-statistic__head) { font-size: 13px; }
.cost-dashboard-page :deep(.el-statistic__content) { font-size: 20px; }
.trend-bar {
  background: linear-gradient(180deg, #409eff 0%, #79bbff 100%);
  border-radius: 4px 4px 0 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-height: 4px;
  position: relative;
}
.trend-value {
  font-size: 11px;
  color: #fff;
  white-space: nowrap;
  padding-top: 2px;
}
</style>
