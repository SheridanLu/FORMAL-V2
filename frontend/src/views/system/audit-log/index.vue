<template>
  <div class="audit-log-page">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="操作模块">
          <el-input v-model="queryForm.operateModule" placeholder="请输入模块名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operateType" placeholder="全部" clearable style="width: 130px">
            <el-option label="新增" value="CREATE" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="查询" value="QUERY" />
            <el-option label="审批" value="APPROVE" />
            <el-option label="导出" value="EXPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" style="margin-top: 12px">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="user_name" label="操作人" width="110" />
        <el-table-column prop="operate_module" label="操作模块" width="120" />
        <el-table-column prop="operate_type" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.operate_type] || 'info'" size="small">
              {{ typeTextMap[row.operate_type] || row.operate_type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="biz_type" label="业务类型" width="120" />
        <el-table-column prop="biz_id" label="业务ID" width="80" />
        <el-table-column prop="ip_address" label="IP地址" width="140" />
        <el-table-column prop="request_id" label="请求ID" width="180" show-overflow-tooltip />
        <el-table-column prop="created_at" label="操作时间" width="170" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        style="margin-top: 16px; justify-content: flex-end"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-sizes="[10, 20, 50]"
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="审计日志详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentRow.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentRow.user_name }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ currentRow.operate_module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ currentRow.operate_type }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ currentRow.biz_type }}</el-descriptions-item>
        <el-descriptions-item label="业务ID">{{ currentRow.biz_id }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentRow.ip_address }}</el-descriptions-item>
        <el-descriptions-item label="请求ID">{{ currentRow.request_id }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ currentRow.created_at }}</el-descriptions-item>
      </el-descriptions>
      <template v-if="currentRow.before_data || currentRow.after_data">
        <el-divider />
        <el-row :gutter="16">
          <el-col :span="12" v-if="currentRow.before_data">
            <h4>变更前数据</h4>
            <pre class="json-block">{{ formatJson(currentRow.before_data) }}</pre>
          </el-col>
          <el-col :span="12" v-if="currentRow.after_data">
            <h4>变更后数据</h4>
            <pre class="json-block">{{ formatJson(currentRow.after_data) }}</pre>
          </el-col>
        </el-row>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAuditLogList } from '@/api/auditLog'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref(null)
const detailVisible = ref(false)
const currentRow = ref({})

const queryForm = reactive({
  operateModule: '',
  operateType: '',
  page: 1,
  size: 20
})

const typeTagMap = {
  CREATE: 'success',
  UPDATE: 'warning',
  DELETE: 'danger',
  QUERY: 'info',
  APPROVE: 'primary',
  EXPORT: ''
}

const typeTextMap = {
  CREATE: '新增',
  UPDATE: '修改',
  DELETE: '删除',
  QUERY: '查询',
  APPROVE: '审批',
  EXPORT: '导出'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryForm }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getAuditLogList(params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryForm.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.operateModule = ''
  queryForm.operateType = ''
  dateRange.value = null
  queryForm.page = 1
  fetchData()
}

const handleDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

const formatJson = (str) => {
  if (!str) return ''
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 0;
}
.json-block {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  font-size: 12px;
  max-height: 300px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
