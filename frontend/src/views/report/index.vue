<template>
  <div class="page-container">
    <page-header title="报表分析" />

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 项目进度总览 -->
      <el-tab-pane label="项目概览" name="project">
        <div class="filter-bar">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:320px" @change="fetchProjectData" />
        </div>
        <el-row :gutter="16" class="stat-cards">
          <el-col :span="6"><el-statistic title="项目总数" :value="projectStats.total" /></el-col>
          <el-col :span="6"><el-statistic title="进行中" :value="projectStats.active" /></el-col>
          <el-col :span="6"><el-statistic title="已完成" :value="projectStats.completed" /></el-col>
          <el-col :span="6"><el-statistic title="逾期项目" :value="projectStats.overdue" /></el-col>
        </el-row>
        <div ref="projectChartRef" class="chart-container"></div>
      </el-tab-pane>

      <!-- 财务收支 -->
      <el-tab-pane label="财务报表" name="finance">
        <div class="filter-bar">
          <el-date-picker v-model="finDateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:320px" @change="fetchFinanceData" />
        </div>
        <el-row :gutter="16" class="stat-cards">
          <el-col :span="6"><el-statistic title="总收入" :value="finStats.income" :precision="2" prefix="¥" /></el-col>
          <el-col :span="6"><el-statistic title="总支出" :value="finStats.expense" :precision="2" prefix="¥" /></el-col>
          <el-col :span="6"><el-statistic title="利润" :value="finStats.profit" :precision="2" prefix="¥" /></el-col>
          <el-col :span="6"><el-statistic title="利润率" :value="finStats.profitRate" :precision="1" suffix="%" /></el-col>
        </el-row>
        <div ref="financeChartRef" class="chart-container"></div>
      </el-tab-pane>

      <!-- 物资库存 -->
      <el-tab-pane label="库存报表" name="inventory">
        <div ref="inventoryChartRef" class="chart-container"></div>
      </el-tab-pane>

      <!-- 合同执行 -->
      <el-tab-pane label="合同报表" name="contract">
        <div ref="contractChartRef" class="chart-container"></div>
      </el-tab-pane>

      <!-- 成本分析 -->
      <el-tab-pane label="成本分析" name="cost">
        <div class="filter-bar">
          <project-select v-model="costProjectId" placeholder="按项目筛选" @change="fetchCostData" style="width:240px" />
        </div>
        <div ref="costChartRef" class="chart-container"></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getProjectOverview, getFinanceReport, getInventoryReport, getContractReport, getCostAnalysis } from '@/api/report'

const activeTab = ref('project')

// ===== 项目概览 =====
const dateRange = ref([])
const projectChartRef = ref(null)
const projectStats = reactive({ total: 0, active: 0, completed: 0, overdue: 0 })
let projectChart = null

async function fetchProjectData() {
  try {
    const params = {}
    if (dateRange.value?.length === 2) {
      params.start_date = dateRange.value[0]
      params.end_date = dateRange.value[1]
    }
    const res = await getProjectOverview(params)
    const d = res.data || {}
    Object.assign(projectStats, { total: d.total || 0, active: d.active || 0, completed: d.completed || 0, overdue: d.overdue || 0 })
    renderProjectChart(d)
  } catch { ElMessage.error('获取项目概览失败') }
}

function renderProjectChart(data) {
  if (!projectChartRef.value) return
  if (!projectChart) projectChart = echarts.init(projectChartRef.value)
  projectChart.setOption({
    title: { text: '项目状态分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: data?.active || 0, name: '进行中' },
        { value: data?.completed || 0, name: '已完成' },
        { value: data?.suspended || 0, name: '已暂停' },
        { value: data?.overdue || 0, name: '逾期' }
      ]
    }]
  })
}

// ===== 财务 =====
const finDateRange = ref([])
const financeChartRef = ref(null)
const finStats = reactive({ income: 0, expense: 0, profit: 0, profitRate: 0 })
let financeChart = null

async function fetchFinanceData() {
  try {
    const params = {}
    if (finDateRange.value?.length === 2) {
      params.start_date = finDateRange.value[0]
      params.end_date = finDateRange.value[1]
    }
    const res = await getFinanceReport(params)
    const d = res.data || {}
    Object.assign(finStats, d)
    renderFinanceChart(d)
  } catch { ElMessage.error('获取财务报表失败') }
}

function renderFinanceChart(data) {
  if (!financeChartRef.value) return
  if (!financeChart) financeChart = echarts.init(financeChartRef.value)
  const months = data?.months || []
  financeChart.setOption({
    title: { text: '月度收支趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, data: ['收入', '支出', '利润'] },
    xAxis: { type: 'category', data: months.map(m => m.month) },
    yAxis: { type: 'value' },
    series: [
      { name: '收入', type: 'bar', data: months.map(m => m.income) },
      { name: '支出', type: 'bar', data: months.map(m => m.expense) },
      { name: '利润', type: 'line', data: months.map(m => m.profit) }
    ]
  })
}

// ===== 库存 =====
const inventoryChartRef = ref(null)
let inventoryChart = null

async function fetchInventoryData() {
  try {
    const res = await getInventoryReport()
    renderInventoryChart(res.data)
  } catch { ElMessage.error('获取库存报表失败') }
}

function renderInventoryChart(data) {
  if (!inventoryChartRef.value) return
  if (!inventoryChart) inventoryChart = echarts.init(inventoryChartRef.value)
  const items = data?.items || []
  inventoryChart.setOption({
    title: { text: '库存分布', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: items.map(i => i.category), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [{ type: 'bar', data: items.map(i => i.amount), barMaxWidth: 40 }]
  })
}

// ===== 合同 =====
const contractChartRef = ref(null)
let contractChart = null

async function fetchContractData() {
  try {
    const res = await getContractReport()
    renderContractChart(res.data)
  } catch { ElMessage.error('获取合同报表失败') }
}

function renderContractChart(data) {
  if (!contractChartRef.value) return
  if (!contractChart) contractChart = echarts.init(contractChartRef.value)
  contractChart.setOption({
    title: { text: '合同执行概况', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: data?.active || 0, name: '执行中' },
        { value: data?.completed || 0, name: '已完成' },
        { value: data?.terminated || 0, name: '已终止' }
      ]
    }]
  })
}

// ===== 成本 =====
const costProjectId = ref('')
const costChartRef = ref(null)
let costChart = null

async function fetchCostData() {
  try {
    const params = {}
    if (costProjectId.value) params.project_id = costProjectId.value
    const res = await getCostAnalysis(params)
    renderCostChart(res.data)
  } catch { ElMessage.error('获取成本分析失败') }
}

function renderCostChart(data) {
  if (!costChartRef.value) return
  if (!costChart) costChart = echarts.init(costChartRef.value)
  const items = data?.items || []
  costChart.setOption({
    title: { text: '成本构成', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['30%', '65%'],
      roseType: 'area',
      data: items.map(i => ({ value: i.amount, name: i.category }))
    }]
  })
}

// Tab 切换时初始化对应图表
watch(activeTab, async (tab) => {
  await nextTick()
  if (tab === 'inventory') fetchInventoryData()
  else if (tab === 'contract') fetchContractData()
  else if (tab === 'cost') fetchCostData()
})

onMounted(() => {
  fetchProjectData()
  fetchFinanceData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  projectChart?.dispose()
  financeChart?.dispose()
  inventoryChart?.dispose()
  contractChart?.dispose()
  costChart?.dispose()
})

function handleResize() {
  projectChart?.resize()
  financeChart?.resize()
  inventoryChart?.resize()
  contractChart?.resize()
  costChart?.resize()
}
</script>

<style scoped lang="scss">
.page-container { background: #fff; border-radius: 4px; padding: 20px; }
.filter-bar { margin-bottom: 16px; }
.stat-cards { margin-bottom: 20px; }
.chart-container { height: 400px; width: 100%; }
</style>
