<template>
  <div>
    <div class="toolbar">
      <project-select v-model="query.projectId" placeholder="按项目筛选" clearable style="width: 200px; margin-right: 8px" />
      <material-select v-model="query.materialId" placeholder="按材料筛选" clearable style="width: 200px; margin-right: 8px" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="material_name" label="材料名称" min-width="140" />
      <el-table-column prop="spec" label="规格" width="120" />
      <el-table-column prop="unit" label="单位" width="80" align="center" />
      <el-table-column prop="current_quantity" label="当前库存" width="120" align="right" />
      <el-table-column prop="avg_price" label="均价" width="120" align="right" />
      <el-table-column label="库存金额" width="130" align="right">
        <template #default="{ row }"><money-text :value="row.total_amount" /></template>
      </el-table-column>
      <el-table-column prop="updated_at" label="更新时间" width="170" />
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { getStockList } from '@/api/inventory'
import { useTable } from '@/composables/useTable'

const { loading, tableData, total, query, fetchData, handleSearch, handleReset, handleSizeChange, handleCurrentChange } = useTable(getStockList, { projectId: null, materialId: null })

fetchData()
</script>
