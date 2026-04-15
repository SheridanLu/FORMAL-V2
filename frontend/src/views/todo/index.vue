<template>
  <div class="todo-page">
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryStatus" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="待办标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="biz_type" label="业务类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ bizTypeLabel(row.biz_type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="摘要" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'" size="small">
              {{ row.status === 0 ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="170" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.biz_type && row.biz_id" @click="handleGoto(row)">查看源单</el-button>
            <el-button v-if="row.status === 0" link type="warning" @click="handleDone(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 待办详情弹窗 -->
    <el-dialog v-model="detailVisible" title="待办详情" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="待办标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ bizTypeLabel(detailData.biz_type) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 0 ? 'warning' : 'success'" size="small">
            {{ detailData.status === 0 ? '待处理' : '已处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detailData.content || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.created_at }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailData.read_at || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button v-if="detailData && detailData.biz_type && detailData.biz_id" type="primary" @click="handleGoto(detailData)">查看源单据</el-button>
        <el-button v-if="detailData && detailData.status === 0" type="warning" @click="handleDoneFromDetail">标记处理</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTodoList, getTodoDetail, markTodoDone } from '@/api/todo'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const queryStatus = ref(null)
const detailVisible = ref(false)
const detailData = ref(null)

/** 业务类型中文映射 */
const BIZ_TYPE_MAP = {
  approval: '审批',
  project: '项目',
  contract: '合同',
  payment: '付款',
  receipt: '收款',
  reimbursement: '报销',
  change_order: '变更单',
  completion: '竣工验收',
  progress: '进度',
  supplier: '供应商'
}

const bizTypeLabel = (type) => BIZ_TYPE_MAP[type] || type || '-'

/** 源单据跳转路由映射 */
const BIZ_ROUTE_MAP = {
  project: (id) => `/projects/${id}`,
  contract: (id) => `/contracts/${id}`,
  approval: (id) => `/approvals/${id}`,
  payment: (id) => `/finance/payments/${id}`,
  receipt: (id) => `/finance/receipts/${id}`,
  change_order: (id) => `/progress/changes/${id}`
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (queryStatus.value !== null && queryStatus.value !== '') {
      params.status = queryStatus.value
    }
    const res = await getTodoList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { page.value = 1; fetchData() }

const handleDetail = async (row) => {
  try {
    const res = await getTodoDetail(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch {
    ElMessage.error('获取详情失败')
  }
}

const handleGoto = (row) => {
  const routeFn = BIZ_ROUTE_MAP[row.biz_type]
  if (routeFn && row.biz_id) {
    router.push(routeFn(row.biz_id))
  } else {
    ElMessage.info('暂不支持跳转该类型的源单据')
  }
}

const handleDone = async (row) => {
  await markTodoDone(row.id)
  ElMessage.success('已标记处理')
  fetchData()
}

const handleDoneFromDetail = async () => {
  if (!detailData.value) return
  await markTodoDone(detailData.value.id)
  ElMessage.success('已标记处理')
  detailData.value.status = 1
  fetchData()
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
