<template>
  <div class="app-container">
    <page-header title="供应商评价">
      <el-button v-permission="'supplier:rating'" type="primary" @click="openCreate">新增评价</el-button>
    </page-header>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="评价记录" name="list">
        <el-form inline style="margin-bottom: 12px;">
          <el-form-item label="供应商">
            <el-input v-model.number="supplierId" placeholder="输入供应商ID" clearable style="width:160px;" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchList">查询</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="tableData" border stripe>
          <el-table-column prop="supplier_id" label="供应商ID" width="100" />
          <el-table-column prop="quality_score" label="质量" width="70" align="center" />
          <el-table-column prop="delivery_score" label="交货" width="70" align="center" />
          <el-table-column prop="service_score" label="服务" width="70" align="center" />
          <el-table-column prop="price_score" label="价格" width="70" align="center" />
          <el-table-column prop="total_score" label="综合评分" width="90" align="center">
            <template #default="{ row }">
              <el-tag type="success">{{ row.total_score }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="comment_text" label="评价内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="created_at" label="评价时间" width="160" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button v-permission="'supplier:rating'" link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="综合评分" name="summary">
        <el-table v-loading="summaryLoading" :data="summaryData" border stripe>
          <el-table-column prop="supplierName" label="供应商" min-width="160" />
          <el-table-column prop="ratingCount" label="评价次数" width="90" align="center" />
          <el-table-column prop="avgQuality" label="平均质量" width="100" align="center">
            <template #default="{ row }">{{ row.avgQuality ? row.avgQuality.toFixed(2) : '-' }}</template>
          </el-table-column>
          <el-table-column prop="avgDelivery" label="平均交货" width="100" align="center">
            <template #default="{ row }">{{ row.avgDelivery ? row.avgDelivery.toFixed(2) : '-' }}</template>
          </el-table-column>
          <el-table-column prop="avgService" label="平均服务" width="100" align="center">
            <template #default="{ row }">{{ row.avgService ? row.avgService.toFixed(2) : '-' }}</template>
          </el-table-column>
          <el-table-column prop="avgTotal" label="综合均分" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.avgTotal" :type="row.avgTotal >= 4 ? 'success' : row.avgTotal >= 3 ? 'warning' : 'danger'">
                {{ row.avgTotal.toFixed(2) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增评价弹窗 -->
    <el-dialog v-model="formVisible" title="新增供应商评价" width="500px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="供应商ID" required>
          <el-input v-model.number="form.supplierId" type="number" />
        </el-form-item>
        <el-form-item label="质量评分">
          <el-rate v-model="form.qualityScore" :max="5" />
        </el-form-item>
        <el-form-item label="交货评分">
          <el-rate v-model="form.deliveryScore" :max="5" />
        </el-form-item>
        <el-form-item label="服务评分">
          <el-rate v-model="form.serviceScore" :max="5" />
        </el-form-item>
        <el-form-item label="价格评分">
          <el-rate v-model="form.priceScore" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.commentText" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSupplierRatingList, getSupplierRatingSummary, createSupplierRating, deleteSupplierRating } from '@/api/supplier'

const activeTab = ref('list')
const loading = ref(false)
const tableData = ref([])
const supplierId = ref(null)
const summaryLoading = ref(false)
const summaryData = ref([])
const formVisible = ref(false)
const submitting = ref(false)
const form = reactive({ supplierId: null, qualityScore: 5, deliveryScore: 5, serviceScore: 5, priceScore: 5, commentText: '' })

async function fetchList() {
  loading.value = true
  try {
    const res = await getSupplierRatingList({ supplierId: supplierId.value || undefined })
    tableData.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

async function fetchSummary() {
  summaryLoading.value = true
  try {
    const res = await getSupplierRatingSummary()
    summaryData.value = res.data || []
  } finally {
    summaryLoading.value = false
  }
}

watch(activeTab, val => { if (val === 'summary') fetchSummary() })

function openCreate() {
  Object.assign(form, { supplierId: null, qualityScore: 5, deliveryScore: 5, serviceScore: 5, priceScore: 5, commentText: '' })
  formVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    await createSupplierRating(form)
    ElMessage.success('评价提交成功')
    formVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该评价？', '提示', { type: 'warning' })
  await deleteSupplierRating(row.id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>
