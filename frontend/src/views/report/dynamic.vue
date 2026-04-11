<template>
  <div class="app-container">
    <page-header :title="templateName || '动态报表'">
      <el-button @click="$router.back()">返回</el-button>
    </page-header>

    <!-- 内置报表选择 -->
    <el-card v-if="!templateId" shadow="never" style="margin-bottom:16px">
      <template #header>内置报表</template>
      <el-space wrap>
        <el-button @click="loadBuiltin('stock-flow')">进销存流水</el-button>
        <el-button @click="loadBuiltin('stock-aging')">库龄分析</el-button>
        <el-button @click="loadBuiltin('purchase-price')">采购价格对比</el-button>
      </el-space>
    </el-card>

    <!-- 动态参数 -->
    <el-card v-if="paramsDef.length" shadow="never" style="margin-bottom:16px">
      <template #header>查询参数</template>
      <el-form inline>
        <el-form-item v-for="p in paramsDef" :key="p.name" :label="p.label || p.name">
          <el-input-number v-if="p.type === 'number'" v-model="paramValues[p.name]" :placeholder="p.label" />
          <el-date-picker v-else-if="p.type === 'date'" v-model="paramValues[p.name]" type="date" value-format="YYYY-MM-DD" />
          <el-input v-else v-model="paramValues[p.name]" :placeholder="p.label" style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="execute">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" v-loading="loading">
      <!-- 图表区 -->
      <div v-if="chartVisible && chartType !== 'table'" ref="chartRef" style="height:360px;margin-bottom:16px" />

      <!-- 表格区 -->
      <el-table v-if="tableRows.length" :data="tableRows" border stripe size="small" style="margin-top:8px">
        <el-table-column v-for="col in tableColumns" :key="col" :prop="col" :label="col" show-overflow-tooltip />
      </el-table>
      <el-empty v-else-if="!loading" description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getReportTemplate, executeReportTemplate,
  getStockFlowReport, getStockAgingReport, getPurchasePriceComparison
} from '@/api/report'

const route = useRoute()
const templateId = computed(() => route.query.id)
const templateName = ref('')
const chartType = ref('table')
const xField = ref('')
const yFields = ref([])
const paramsDef = ref([])
const paramValues = reactive({})
const loading = ref(false)
const tableRows = ref([])
const tableColumns = ref([])
const chartRef = ref(null)
const chartVisible = ref(false)
let chartInstance = null

async function loadTemplate() {
  if (!templateId.value) return
  const res = await getReportTemplate(templateId.value)
  const t = res.data
  templateName.value = t.report_name
  chartType.value = t.chart_type || 'table'
  xField.value = t.x_field || ''
  yFields.value = t.y_fields ? t.y_fields.split(',').map(s => s.trim()) : []
  if (t.params_json) {
    try { paramsDef.value = JSON.parse(t.params_json) } catch {}
  }
  execute()
}

async function execute() {
  loading.value = true
  try {
    let rows
    if (templateId.value) {
      const res = await executeReportTemplate(templateId.value, { ...paramValues })
      rows = res.data || []
    }
    if (!rows || !rows.length) { tableRows.value = []; tableColumns.value = []; return }
    tableColumns.value = Object.keys(rows[0])
    tableRows.value = rows
    if (chartType.value !== 'table') {
      chartVisible.value = true
      await nextTick()
      renderChart(rows)
    }
  } finally {
    loading.value = false
  }
}

async function loadBuiltin(type) {
  loading.value = true
  chartType.value = 'table'
  try {
    let res
    if (type === 'stock-flow') { res = await getStockFlowReport(); templateName.value = '进销存流水' }
    else if (type === 'stock-aging') { res = await getStockAgingReport(); templateName.value = '库龄分析' }
    else if (type === 'purchase-price') { res = await getPurchasePriceComparison(); templateName.value = '采购价格对比' }
    const rows = res?.data || []
    tableColumns.value = rows.length ? Object.keys(rows[0]) : []
    tableRows.value = rows
  } finally {
    loading.value = false
  }
}

async function renderChart(rows) {
  try {
    const echarts = await import('echarts')
    if (chartInstance) chartInstance.dispose()
    chartInstance = echarts.init(chartRef.value)
    const categories = rows.map(r => r[xField.value] ?? '')
    const series = yFields.value.map(field => ({
      name: field,
      type: chartType.value === 'pie' ? 'pie' : chartType.value,
      data: chartType.value === 'pie'
        ? rows.map(r => ({ name: r[xField.value], value: r[field] }))
        : rows.map(r => r[field])
    }))
    const option = chartType.value === 'pie'
      ? { tooltip: { trigger: 'item' }, legend: { orient: 'vertical', left: 'left' }, series }
      : {
          tooltip: { trigger: 'axis' },
          legend: { data: yFields.value },
          xAxis: { type: 'category', data: categories },
          yAxis: { type: 'value' },
          series
        }
    chartInstance.setOption(option)
  } catch {
    // echarts not installed, skip chart rendering
  }
}

onMounted(() => {
  if (templateId.value) loadTemplate()
})
</script>
