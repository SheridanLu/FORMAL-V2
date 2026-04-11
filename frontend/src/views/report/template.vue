<template>
  <div class="app-container">
    <page-header title="报表模板管理">
      <el-button v-permission="'report:template-manage'" type="primary" @click="openCreate">新增模板</el-button>
    </page-header>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="report_name" label="报表名称" min-width="160" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="chart_type" label="图表类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small">{{ chartTypeLabel(row.chart_type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="x_field" label="X轴字段" width="110" />
      <el-table-column prop="y_fields" label="Y轴字段" width="160" show-overflow-tooltip />
      <el-table-column prop="created_at" label="创建时间" width="160" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="handlePreview(row)">执行</el-button>
          <el-button v-permission="'report:template-manage'" link type="warning" @click="openEdit(row)">编辑</el-button>
          <el-button v-permission="'report:template-manage'" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="editingId ? '编辑报表模板' : '新增报表模板'" width="680px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="报表名称" required>
          <el-input v-model="form.reportName" placeholder="如：月度库存汇总" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="如：库存、采购、财务" style="width:200px" />
        </el-form-item>
        <el-form-item label="图表类型">
          <el-select v-model="form.chartType" style="width:160px">
            <el-option v-for="t in chartTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="X轴字段">
          <el-input v-model="form.xField" placeholder="SQL结果集中的字段名" style="width:200px" />
        </el-form-item>
        <el-form-item label="Y轴字段">
          <el-input v-model="form.yFields" placeholder="逗号分隔，如：amount,qty" />
        </el-form-item>
        <el-form-item label="查询参数">
          <el-input v-model="form.paramsJson" type="textarea" :rows="3"
            placeholder='JSON格式，如：[{"name":"projectId","label":"项目ID","type":"number"}]' />
          <div class="form-tip">参数在SQL中用 :paramName 引用</div>
        </el-form-item>
        <el-form-item label="SQL语句" required>
          <el-input v-model="form.sqlText" type="textarea" :rows="8"
            placeholder="SELECT ... FROM ... WHERE ..." />
          <div class="form-tip">仅支持SELECT语句，可使用 :参数名 引用查询参数</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getReportTemplateList, createReportTemplate, updateReportTemplate, deleteReportTemplate
} from '@/api/report'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const formVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)

const chartTypes = [
  { value: 'bar', label: '柱状图' },
  { value: 'line', label: '折线图' },
  { value: 'pie', label: '饼图' },
  { value: 'radar', label: '雷达图' },
  { value: 'scatter', label: '散点图' },
  { value: 'table', label: '纯表格' }
]

function chartTypeLabel(type) {
  return chartTypes.find(t => t.value === type)?.label || type || '-'
}

const form = reactive({
  reportName: '', category: '', chartType: 'bar',
  xField: '', yFields: '', paramsJson: '', sqlText: ''
})

async function fetchList() {
  loading.value = true
  try {
    tableData.value = (await getReportTemplateList()).data || []
  } catch {
    ElMessage.error('加载模板列表失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { reportName: '', category: '', chartType: 'bar', xField: '', yFields: '', paramsJson: '', sqlText: '' })
  formVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, {
    reportName: row.report_name, category: row.category || '',
    chartType: row.chart_type || 'bar', xField: row.x_field || '',
    yFields: row.y_fields || '', paramsJson: row.params_json || '',
    sqlText: row.sql_text
  })
  formVisible.value = true
}

async function handleSubmit() {
  if (!form.reportName || !form.sqlText) { ElMessage.warning('请填写报表名称和SQL语句'); return }
  submitting.value = true
  try {
    if (editingId.value) {
      await updateReportTemplate(editingId.value, form)
    } else {
      await createReportTemplate(form)
    }
    ElMessage.success('保存成功')
    formVisible.value = false
    fetchList()
  } catch {
    ElMessage.error('保存失败，请检查表单内容')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除报表「${row.report_name}」？`, '提示', { type: 'warning' })
    await deleteReportTemplate(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

function handlePreview(row) {
  router.push({ path: '/report/dynamic', query: { id: row.id } })
}

onMounted(fetchList)
</script>

<style scoped>
.form-tip { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
