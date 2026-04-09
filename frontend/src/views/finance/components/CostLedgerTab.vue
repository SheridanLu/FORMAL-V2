<template>
  <div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="project_name" label="项目" />
      <el-table-column prop="cost_type" label="费用类型" width="120" />
      <el-table-column prop="cost_subtype" label="费用子类" width="120" />
      <el-table-column label="金额" width="130" align="right">
        <template #default="{ row }"><money-text :value="row.amount" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { getCostLedgerList } from '@/api/finance'
import { useTable } from '@/composables/useTable'

const { loading, tableData, total, query, fetchData, handleSizeChange, handleCurrentChange } = useTable(getCostLedgerList)

fetchData()
</script>
