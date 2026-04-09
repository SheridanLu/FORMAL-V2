<template>
  <div class="completion-page">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="竣工验收" name="finish">
          <FinishTab v-if="activeTab === 'finish'" />
        </el-tab-pane>
        <el-tab-pane label="劳务结算" name="labor">
          <LaborTab v-if="activeTab === 'labor'" />
        </el-tab-pane>
        <el-tab-pane label="案件管理" name="case">
          <CaseTab v-if="activeTab === 'case'" />
        </el-tab-pane>
        <el-tab-pane label="异常工单" name="exception">
          <ExceptionTab v-if="activeTab === 'exception'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FinishTab from './components/FinishTab.vue'
import LaborTab from './components/LaborTab.vue'
import CaseTab from './components/CaseTab.vue'
import ExceptionTab from './components/ExceptionTab.vue'

const activeTab = ref('finish')
</script>
