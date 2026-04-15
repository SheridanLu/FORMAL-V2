<template>
  <div class="system-monitor-page">
    <!-- 系统信息 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-card shadow="never">
          <template #header><span>系统状态</span></template>
          <div style="text-align: center">
            <el-tag :type="health.status === 'UP' ? 'success' : 'danger'" size="large" effect="dark">
              {{ health.status || '未知' }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <template #header><span>JVM内存</span></template>
          <el-progress type="dashboard" :percentage="jvmMemPct" :width="100"
            :status="jvmMemPct > 80 ? 'exception' : ''" />
          <div style="text-align:center;font-size:12px;color:#909399;margin-top:4px">
            {{ formatBytes(jvmMemUsed) }} / {{ formatBytes(jvmMemMax) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <template #header><span>CPU使用率</span></template>
          <el-progress type="dashboard" :percentage="cpuPct" :width="100"
            :status="cpuPct > 80 ? 'exception' : ''" />
          <div style="text-align:center;font-size:12px;color:#909399;margin-top:4px">
            处理器: {{ cpuCount }} 核
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <template #header><span>磁盘空间</span></template>
          <el-progress type="dashboard" :percentage="diskPct" :width="100"
            :status="diskPct > 90 ? 'exception' : ''" />
          <div style="text-align:center;font-size:12px;color:#909399;margin-top:4px">
            {{ formatBytes(diskUsed) }} / {{ formatBytes(diskTotal) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 组件健康 -->
    <el-card shadow="never" style="margin-bottom: 16px">
      <template #header><span>组件健康检查</span></template>
      <el-table :data="healthComponents" stripe border size="small">
        <el-table-column prop="name" label="组件" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'UP' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="details" label="详情" min-width="300">
          <template #default="{ row }">
            <span style="font-size:12px;color:#606266">{{ row.details }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- JVM详细指标 -->
    <el-card shadow="never">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>运行时指标</span>
          <el-button type="primary" size="small" @click="refreshAll" :loading="loading">刷新</el-button>
        </div>
      </template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="JVM版本">{{ info.javaVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="运行时间">{{ uptimeStr }}</el-descriptions-item>
        <el-descriptions-item label="活跃线程数">{{ threadCount }}</el-descriptions-item>
        <el-descriptions-item label="GC次数">{{ gcCount }}</el-descriptions-item>
        <el-descriptions-item label="GC耗时">{{ gcTime }}ms</el-descriptions-item>
        <el-descriptions-item label="已加载类">{{ classesLoaded }}</el-descriptions-item>
        <el-descriptions-item label="数据库连接(活跃)">{{ dbActiveConn }}</el-descriptions-item>
        <el-descriptions-item label="数据库连接(最大)">{{ dbMaxConn }}</el-descriptions-item>
        <el-descriptions-item label="HTTP请求总数">{{ httpRequests }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const health = reactive({ status: '', components: {} })
const info = reactive({ javaVersion: '' })

// Metrics
const jvmMemUsed = ref(0)
const jvmMemMax = ref(0)
const cpuPct = ref(0)
const cpuCount = ref(0)
const diskUsed = ref(0)
const diskTotal = ref(0)
const threadCount = ref(0)
const gcCount = ref(0)
const gcTime = ref(0)
const classesLoaded = ref(0)
const dbActiveConn = ref(0)
const dbMaxConn = ref(0)
const httpRequests = ref(0)

const jvmMemPct = computed(() => {
  if (!jvmMemMax.value) return 0
  return Math.round(jvmMemUsed.value / jvmMemMax.value * 100)
})

const diskPct = computed(() => {
  if (!diskTotal.value) return 0
  return Math.round(diskUsed.value / diskTotal.value * 100)
})

const uptimeStr = computed(() => {
  // 从 metrics 获取的 uptime 是秒
  return '-' // Will be populated from process.uptime metric
})

const healthComponents = computed(() => {
  const comps = health.components || {}
  return Object.entries(comps).map(([name, val]) => ({
    name,
    status: val?.status || 'UNKNOWN',
    details: val?.details ? JSON.stringify(val.details) : ''
  }))
})

const formatBytes = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let idx = 0
  let val = bytes
  while (val >= 1024 && idx < units.length - 1) { val /= 1024; idx++ }
  return val.toFixed(1) + ' ' + units[idx]
}

const fetchMetric = async (name) => {
  try {
    const res = await request.get(`/api/v1/actuator/metrics/${name}`)
    const data = res.data || res
    return data?.measurements?.[0]?.value ?? 0
  } catch { return 0 }
}

const refreshAll = async () => {
  loading.value = true
  try {
    // Health
    try {
      const hRes = await request.get('/api/v1/actuator/health')
      const hData = hRes.data || hRes
      health.status = hData.status || 'UNKNOWN'
      health.components = hData.components || {}
    } catch { health.status = 'DOWN' }

    // Metrics (best-effort, some may not exist)
    jvmMemUsed.value = await fetchMetric('jvm.memory.used')
    jvmMemMax.value = await fetchMetric('jvm.memory.max')
    cpuPct.value = Math.round((await fetchMetric('system.cpu.usage')) * 100)
    cpuCount.value = await fetchMetric('system.cpu.count')
    diskTotal.value = await fetchMetric('disk.total')
    diskUsed.value = diskTotal.value - (await fetchMetric('disk.free'))
    threadCount.value = await fetchMetric('jvm.threads.live')
    gcCount.value = await fetchMetric('jvm.gc.pause.count') || 0
    gcTime.value = Math.round(await fetchMetric('jvm.gc.pause.total.time') || 0)
    classesLoaded.value = await fetchMetric('jvm.classes.loaded')
    dbActiveConn.value = await fetchMetric('hikaricp.connections.active')
    dbMaxConn.value = await fetchMetric('hikaricp.connections.max')
    httpRequests.value = await fetchMetric('http.server.requests.count') || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => refreshAll())
</script>

<style scoped>
.system-monitor-page :deep(.el-card__header) {
  padding: 12px 16px;
  font-weight: 500;
}
.system-monitor-page :deep(.el-progress__text) {
  font-size: 16px !important;
}
</style>
