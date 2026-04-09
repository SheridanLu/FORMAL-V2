<template>
  <div class="inventory-page">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="入库管理" name="inbound"><inbound-tab /></el-tab-pane>
        <el-tab-pane label="出库管理" name="outbound"><outbound-tab /></el-tab-pane>
        <el-tab-pane label="退货管理" name="return"><return-tab /></el-tab-pane>
        <el-tab-pane label="库存查询" name="stock"><stock-tab /></el-tab-pane>
        <el-tab-pane label="盘点管理" name="check"><check-tab /></el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import InboundTab from './components/InboundTab.vue'
import OutboundTab from './components/OutboundTab.vue'
import ReturnTab from './components/ReturnTab.vue'
import StockTab from './components/StockTab.vue'
import CheckTab from './components/CheckTab.vue'

const activeTab = ref('inbound')
</script>

<style scoped>
.inventory-page { padding: 0; }
</style>
