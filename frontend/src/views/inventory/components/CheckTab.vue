<template>
  <div>
    <div class="toolbar">
      <project-select v-model="query.projectId" placeholder="按项目筛选" clearable style="width: 200px; margin-right: 8px" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button v-permission="'inventory:check'" type="primary" @click="handleAddCheck" style="margin-left: auto">新建盘点单</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="check_no" label="盘点单号" width="160" />
      <el-table-column prop="project_name" label="关联项目" min-width="120" />
      <el-table-column prop="check_date" label="盘点日期" width="120" />
      <el-table-column prop="item_count" label="盘点项数" width="90" align="center" />
      <el-table-column prop="gain_count" label="盘盈" width="80" align="center">
        <template #default="{ row }"><span style="color:#67c23a">{{ row.gain_count || 0 }}</span></template>
      </el-table-column>
      <el-table-column prop="loss_count" label="盘亏" width="80" align="center">
        <template #default="{ row }"><span style="color:#f56c6c">{{ row.loss_count || 0 }}</span></template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status === 'draft'" v-permission="'inventory:check-approve'" type="success" link size="small" @click="handleSubmitCheck(row)">提交</el-button>
          <el-button v-if="row.status === 'draft'" v-permission="'inventory:check'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { getCheckList, createCheck, deleteCheck, submitCheck } from '@/api/inventory'
import { useTable } from '@/composables/useTable'

const { loading, tableData, total, query, fetchData, handleSearch, handleReset, handleSizeChange, handleCurrentChange } = useTable(getCheckList, { projectId: null })

const handleAddCheck = () => {
  ElMessageBox.confirm('将根据当前库存数据创建盘点单，确认创建？', '提示', { type: 'info' }).then(async () => {
    await createCheck({})
    ElMessage.success('盘点单已创建')
    fetchData()
  }).catch(() => {})
}

const handleSubmitCheck = (row) => {
  ElMessageBox.confirm('确认提交盘点单？提交后将进入审批流程。', '提示', { type: 'warning' }).then(async () => {
    await submitCheck(row.id)
    ElMessage.success('提交成功')
    fetchData()
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该盘点单？', '提示', { type: 'warning' }).then(async () => {
    await deleteCheck(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

fetchData()
</script>
