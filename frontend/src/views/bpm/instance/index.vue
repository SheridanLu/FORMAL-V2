<template>
  <div class="app-container">
    <search-form>
      <el-form inline>
        <el-form-item label="业务类型">
          <el-input v-model="query.bizType" placeholder="请输入" clearable @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.result" clearable placeholder="全部">
            <el-option :value="0" label="进行中" />
            <el-option :value="1" label="已通过" />
            <el-option :value="2" label="已驳回" />
            <el-option :value="3" label="已撤回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </search-form>

    <page-header title="流程监控" />

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="process_inst_id" label="实例ID" width="240" show-overflow-tooltip />
      <el-table-column prop="biz_type" label="业务类型" width="130" />
      <el-table-column prop="biz_no" label="业务单号" min-width="140" />
      <el-table-column prop="initiator_name" label="发起人" width="100" />
      <el-table-column prop="result" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="resultType(row.result)" size="small">{{ resultLabel(row.result) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="start_time" label="发起时间" width="160" />
      <el-table-column prop="end_time" label="结束时间" width="160" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0" :current-page="page" :page-size="size" :total="total"
      layout="total, prev, pager, next" @current-change="p => { page = p; fetchData() }"
      style="margin-top: 16px; justify-content: flex-end;"
    />

    <InstanceDetail ref="detailRef" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllInstances } from '@/api/bpm'
import InstanceDetail from './detail.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const query = reactive({ bizType: '', result: null })
const detailRef = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllInstances({ ...query, page: page.value, size: size.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { bizType: '', result: null })
  fetchData()
}

function viewDetail(row) {
  detailRef.value.open(row.process_inst_id)
}

const resultLabel = r => ({ 0: '进行中', 1: '已通过', 2: '已驳回', 3: '已撤回', 4: '已取消' }[r] || '未知')
const resultType = r => ({ 0: '', 1: 'success', 2: 'danger', 3: 'warning', 4: 'info' }[r] || '')

onMounted(fetchData)
</script>
