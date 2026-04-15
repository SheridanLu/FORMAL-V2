<template>
  <div class="hidden-item-page">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="项目">
          <el-select v-model="queryForm.projectId" placeholder="全部" clearable filterable style="width: 200px">
            <el-option v-for="p in projects" :key="p.id" :label="p.project_name || p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.itemType" placeholder="全部" clearable style="width: 120px">
            <el-option label="材料" value="material" />
            <el-option label="人工" value="labor" />
            <el-option label="设备" value="equipment" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="已发现" value="identified" />
            <el-option label="已报价" value="quoted" />
            <el-option label="已审批" value="approved" />
            <el-option label="已结算" value="settled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 12px">
      <!-- 汇总信息 -->
      <el-row :gutter="16" v-if="summary" style="margin-bottom: 16px">
        <el-col :span="8">
          <el-statistic title="暗项总数" :value="summary.count" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="暗项总金额" :value="summary.totalAmount" :precision="2" prefix="¥" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="预估成本" :value="summary.estimatedCost" :precision="2" prefix="¥" />
        </el-col>
      </el-row>

      <div style="margin-bottom: 12px">
        <el-button type="primary" @click="handleAdd" v-permission="'project:view-all'">新增暗项</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="item_name" label="暗项名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="item_type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ typeMap[row.item_type] || row.item_type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="90" align="right" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="unit_price" label="单价" width="100" align="right">
          <template #default="{ row }">{{ row.unit_price ? Number(row.unit_price).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="total_amount" label="总金额" width="120" align="right">
          <template #default="{ row }">{{ row.total_amount ? Number(row.total_amount).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="estimated_cost" label="预估成本" width="120" align="right">
          <template #default="{ row }">{{ row.estimated_cost ? Number(row.estimated_cost).toLocaleString() : '-' }}</template>
        </el-table-column>
        <el-table-column prop="discovery_date" label="发现日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(row, cmd)" style="margin-left:4px">
              <el-button link type="warning" size="small">状态<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="quoted">已报价</el-dropdown-item>
                  <el-dropdown-item command="approved">已审批</el-dropdown-item>
                  <el-dropdown-item command="settled">已结算</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-if="total > 0" style="margin-top: 16px; justify-content: flex-end" background
        layout="total, sizes, prev, pager, next" :total="total" :page-sizes="[10, 20, 50]"
        v-model:current-page="queryForm.page" v-model:page-size="queryForm.size"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>

    <!-- 新增/编辑暗项 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑暗项' : '新增暗项'" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="关联项目" prop="projectId">
              <el-select v-model="form.projectId" filterable placeholder="选择项目" style="width: 100%">
                <el-option v-for="p in projects" :key="p.id" :label="p.project_name || p.projectName" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="暗项类型" prop="itemType">
              <el-select v-model="form.itemType" style="width: 100%">
                <el-option label="材料" value="material" />
                <el-option label="人工" value="labor" />
                <el-option label="设备" value="equipment" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="暗项名称" prop="itemName">
          <el-input v-model="form.itemName" placeholder="如：地下管线改迁" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="数量">
              <el-input-number v-model="form.quantity" :precision="4" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="m/㎡/项" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价">
              <el-input-number v-model="form.unitPrice" :precision="4" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="总金额">
              <el-input-number v-model="form.totalAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预估成本">
              <el-input-number v-model="form.estimatedCost" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发现日期">
              <el-date-picker v-model="form.discoveryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="暗项的详细描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getHiddenItemList, createHiddenItem, updateHiddenItem, updateHiddenItemStatus, deleteHiddenItem, getHiddenItemSummary } from '@/api/hiddenItem'
import { getAllProjects } from '@/api/project'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const projects = ref([])
const summary = ref(null)

const typeMap = { material: '材料', labor: '人工', equipment: '设备', other: '其他' }
const statusMap = {
  identified: { text: '已发现', type: 'info' },
  quoted: { text: '已报价', type: 'warning' },
  approved: { text: '已审批', type: '' },
  settled: { text: '已结算', type: 'success' }
}

const queryForm = reactive({ projectId: null, itemType: '', status: '', page: 1, size: 20 })

const form = reactive({
  projectId: null, contractId: null, itemName: '', itemType: 'other',
  quantity: null, unit: '', unitPrice: null, totalAmount: null,
  estimatedCost: null, discoveryDate: null, description: '', remark: ''
})

const rules = {
  projectId: [{ required: true, message: '请选择关联项目', trigger: 'change' }],
  itemName: [{ required: true, message: '请输入暗项名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHiddenItemList(queryForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const fetchSummary = async () => {
  if (!queryForm.projectId) { summary.value = null; return }
  try {
    const res = await getHiddenItemSummary(queryForm.projectId)
    summary.value = res.data
  } catch { summary.value = null }
}

watch(() => queryForm.projectId, () => fetchSummary())

const handleSearch = () => { queryForm.page = 1; fetchData(); fetchSummary() }
const handleReset = () => {
  queryForm.projectId = null; queryForm.itemType = ''; queryForm.status = ''; queryForm.page = 1
  fetchData(); fetchSummary()
}

const handleAdd = () => {
  isEdit.value = false; editId.value = null
  Object.assign(form, {
    projectId: queryForm.projectId, contractId: null, itemName: '', itemType: 'other',
    quantity: null, unit: '', unitPrice: null, totalAmount: null,
    estimatedCost: null, discoveryDate: null, description: '', remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, {
    projectId: row.project_id, contractId: row.contract_id, itemName: row.item_name,
    itemType: row.item_type, quantity: row.quantity, unit: row.unit,
    unitPrice: row.unit_price, totalAmount: row.total_amount,
    estimatedCost: row.estimated_cost, discoveryDate: row.discovery_date,
    description: row.description || '', remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateHiddenItem(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createHiddenItem(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false; fetchData(); fetchSummary()
  } finally { submitting.value = false }
}

const handleStatusChange = async (row, status) => {
  const label = statusMap[status]?.text || status
  await ElMessageBox.confirm(`确定将状态改为"${label}"？`, '提示', { type: 'warning' })
  await updateHiddenItemStatus(row.id, status)
  ElMessage.success('状态已更新')
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除暗项"${row.item_name}"？`, '提示', { type: 'warning' })
  await deleteHiddenItem(row.id)
  ElMessage.success('删除成功')
  fetchData(); fetchSummary()
}

onMounted(() => {
  fetchData()
  getAllProjects().then(res => { projects.value = res.data || [] })
})
</script>

<style scoped>
.search-card { margin-bottom: 0; }
</style>
