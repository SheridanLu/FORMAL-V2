<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 库存预警 -->
      <el-tab-pane label="预警配置" name="config">
        <page-header title="库存预警配置">
          <el-button v-permission="'inventory:alert-manage'" type="primary" @click="openAlertForm()">新增预警</el-button>
        </page-header>
        <el-table v-loading="alertLoading" :data="alertData" border stripe>
          <el-table-column prop="project_id" label="项目ID" width="90" />
          <el-table-column prop="material_name" label="物料名称" min-width="160" />
          <el-table-column prop="unit" label="单位" width="70" />
          <el-table-column prop="safety_qty" label="安全库存" width="100" align="right" />
          <el-table-column prop="min_qty" label="最低库存" width="100" align="right" />
          <el-table-column prop="alert_enabled" label="预警" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.alert_enabled ? 'success' : 'info'" size="small">
                {{ row.alert_enabled ? '开启' : '关闭' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button v-permission="'inventory:alert-manage'" link type="danger" @click="handleDeleteAlert(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 库存调拨 -->
      <el-tab-pane label="库存调拨" name="transfer">
        <page-header title="库存调拨">
          <el-button v-permission="'inventory:transfer'" type="primary" @click="openTransferForm">新建调拨</el-button>
        </page-header>
        <el-table v-loading="transferLoading" :data="transferData" border stripe>
          <el-table-column prop="transfer_no" label="调拨单号" width="150" />
          <el-table-column prop="from_project_id" label="源项目" width="90" />
          <el-table-column prop="to_project_id" label="目标项目" width="90" />
          <el-table-column prop="material_name" label="物料" min-width="140" />
          <el-table-column prop="qty" label="数量" width="90" align="right" />
          <el-table-column prop="unit" label="单位" width="60" />
          <el-table-column prop="total_amount" label="金额" width="110" align="right" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="{ draft: '', confirmed: 'success', cancelled: 'danger' }[row.status]" size="small">
                {{ { draft: '草稿', confirmed: '已确认', cancelled: '已取消' }[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" align="center">
            <template #default="{ row }">
              <template v-if="row.status === 'draft'">
                <el-button v-permission="'inventory:transfer'" link type="success" @click="handleConfirm(row)">确认</el-button>
                <el-button v-permission="'inventory:transfer'" link type="warning" @click="handleCancel(row)">取消</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 预警配置弹窗 -->
    <el-dialog v-model="alertFormVisible" title="库存预警配置" width="440px" destroy-on-close>
      <el-form :model="alertForm" label-width="100px">
        <el-form-item label="项目ID" required><el-input v-model.number="alertForm.projectId" type="number" /></el-form-item>
        <el-form-item label="物料ID" required><el-input v-model.number="alertForm.materialId" type="number" /></el-form-item>
        <el-form-item label="物料名称"><el-input v-model="alertForm.materialName" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="alertForm.unit" style="width:100px" /></el-form-item>
        <el-form-item label="安全库存"><el-input-number v-model="alertForm.safetyQty" :min="0" /></el-form-item>
        <el-form-item label="最低库存"><el-input-number v-model="alertForm.minQty" :min="0" /></el-form-item>
        <el-form-item label="预警开关">
          <el-switch v-model="alertForm.alertEnabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="alertFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="alertSubmitting" @click="submitAlert">保存</el-button>
      </template>
    </el-dialog>

    <!-- 调拨弹窗 -->
    <el-dialog v-model="transferFormVisible" title="新建库存调拨" width="440px" destroy-on-close>
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="源项目ID" required><el-input v-model.number="transferForm.fromProjectId" type="number" /></el-form-item>
        <el-form-item label="目标项目ID" required><el-input v-model.number="transferForm.toProjectId" type="number" /></el-form-item>
        <el-form-item label="物料ID" required><el-input v-model.number="transferForm.materialId" type="number" /></el-form-item>
        <el-form-item label="调拨数量" required><el-input-number v-model="transferForm.qty" :min="0.01" :precision="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="transferForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="transferSubmitting" @click="submitTransfer">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getInventoryAlerts, saveInventoryAlert, deleteInventoryAlert,
  getInventoryTransfers, createInventoryTransfer, confirmInventoryTransfer, cancelInventoryTransfer
} from '@/api/inventory'

const activeTab = ref('config')
const alertLoading = ref(false)
const alertData = ref([])
const alertFormVisible = ref(false)
const alertSubmitting = ref(false)
const alertForm = reactive({ projectId: null, materialId: null, materialName: '', unit: '', safetyQty: 0, minQty: 0, alertEnabled: 1 })

const transferLoading = ref(false)
const transferData = ref([])
const transferFormVisible = ref(false)
const transferSubmitting = ref(false)
const transferForm = reactive({ fromProjectId: null, toProjectId: null, materialId: null, qty: 1, remark: '' })

async function fetchAlerts() {
  alertLoading.value = true
  try { alertData.value = (await getInventoryAlerts()).data || [] } finally { alertLoading.value = false }
}
async function fetchTransfers() {
  transferLoading.value = true
  try { transferData.value = (await getInventoryTransfers()).data || [] } finally { transferLoading.value = false }
}

watch(activeTab, val => { if (val === 'transfer') fetchTransfers() })

function openAlertForm() { Object.assign(alertForm, { projectId: null, materialId: null, materialName: '', unit: '', safetyQty: 0, minQty: 0, alertEnabled: 1 }); alertFormVisible.value = true }
async function submitAlert() {
  alertSubmitting.value = true
  try { await saveInventoryAlert(alertForm); ElMessage.success('保存成功'); alertFormVisible.value = false; fetchAlerts() } finally { alertSubmitting.value = false }
}
async function handleDeleteAlert(row) {
  await ElMessageBox.confirm('确定删除该预警配置？', '提示', { type: 'warning' })
  await deleteInventoryAlert(row.id); ElMessage.success('删除成功'); fetchAlerts()
}

function openTransferForm() { Object.assign(transferForm, { fromProjectId: null, toProjectId: null, materialId: null, qty: 1, remark: '' }); transferFormVisible.value = true }
async function submitTransfer() {
  transferSubmitting.value = true
  try { await createInventoryTransfer(transferForm); ElMessage.success('调拨单已创建'); transferFormVisible.value = false; fetchTransfers() } finally { transferSubmitting.value = false }
}
async function handleConfirm(row) {
  await ElMessageBox.confirm(`确认调拨「${row.transfer_no}」？将实际扣减源项目库存。`, '提示', { type: 'warning' })
  await confirmInventoryTransfer(row.id); ElMessage.success('调拨已确认'); fetchTransfers()
}
async function handleCancel(row) {
  await cancelInventoryTransfer(row.id); ElMessage.success('已取消'); fetchTransfers()
}

onMounted(fetchAlerts)
</script>
