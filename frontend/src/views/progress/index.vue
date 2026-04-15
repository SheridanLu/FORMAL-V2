<template>
  <div class="progress-page">
    <el-card shadow="never">
      <el-form inline style="margin-bottom: 0">
        <el-form-item label="关联项目">
          <project-select v-model="filterProjectId" clearable placeholder="全部项目" style="width: 220px" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 12px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="里程碑管理" name="milestone">
          <milestone-tab :project-id="filterProjectId" />
        </el-tab-pane>
        <el-tab-pane label="甘特图管理" name="gantt">
          <gantt-tab :project-id="filterProjectId" />
        </el-tab-pane>
        <el-tab-pane label="变更管理" name="change">
          <change-order-tab :project-id="filterProjectId" />
        </el-tab-pane>
        <el-tab-pane label="产值报表" name="statement">
          <statement-tab :project-id="filterProjectId" />
        </el-tab-pane>
        <el-tab-pane label="收入拆分" name="incomeSplit">
          <income-split-tab :project-id="filterProjectId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import MilestoneTab from './components/MilestoneTab.vue'
import GanttTab from './components/GanttTab.vue'
import ChangeOrderTab from './components/ChangeOrderTab.vue'
import StatementTab from './components/StatementTab.vue'
import IncomeSplitTab from './components/IncomeSplitTab.vue'

const activeTab = ref('milestone')
const filterProjectId = ref(null)
</script>
