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
        <el-table-column prop="biz_type" label="业务类型" width="120" />
        <el-table-column prop="content" label="摘要" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'" size="small">
              {{ row.status === 0 ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="handleDone(row)">处理</el-button>
            <span v-else class="done-text">已处理</span>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodoList, markTodoDone } from '@/api/todo'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const queryStatus = ref(null)

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

const handleDone = async (row) => {
  await markTodoDone(row.id)
  ElMessage.success('已标记处理')
  fetchData()
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.done-text { color: #909399; font-size: 13px; }
</style>
