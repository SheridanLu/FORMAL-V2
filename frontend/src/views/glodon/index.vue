<template>
  <div class="glodon-import-page">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>广联达数据导入</span>
          <el-button type="primary" @click="showUpload = true" v-permission="'project:view-all'">导入数据</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="file_name" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="import_type" label="导入类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ typeMap[row.import_type] || row.import_type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="total_rows" label="总行数" width="90" align="center" />
        <el-table-column prop="success_rows" label="成功行数" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="importStatusMap[row.status]?.type" size="small">
              {{ importStatusMap[row.status]?.text || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="error_msg" label="错误信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="created_at" label="导入时间" width="170" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-if="total > 0" style="margin-top: 16px; justify-content: flex-end" background
        layout="total, prev, pager, next" :total="total"
        v-model:current-page="queryForm.page" @current-change="fetchData" />
    </el-card>

    <!-- 上传弹窗 -->
    <el-dialog v-model="showUpload" title="导入广联达数据" width="500px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="关联项目" required>
          <el-select v-model="uploadForm.projectId" filterable placeholder="选择项目" style="width: 100%">
            <el-option v-for="p in projects" :key="p.id" :label="p.project_name || p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="导入类型" required>
          <el-select v-model="uploadForm.importType" style="width: 100%">
            <el-option label="成本数据" value="cost" />
            <el-option label="工程量数据" value="quantity" />
            <el-option label="材料价格" value="price" />
          </el-select>
        </el-form-item>
        <el-form-item label="Excel文件" required>
          <el-upload ref="uploadRef" :auto-upload="false" :limit="1" accept=".xls,.xlsx"
            :on-change="onFileChange" :on-remove="onFileRemove">
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 .xls/.xlsx 格式，广联达导出的清单计价/概算汇总文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpload = false">取消</el-button>
        <el-button type="primary" :loading="uploading" :disabled="!uploadForm.file || !uploadForm.projectId" @click="handleImport">
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGlodonImportList, importGlodonFile, deleteGlodonImport } from '@/api/glodon'
import { getAllProjects } from '@/api/project'

const loading = ref(false)
const uploading = ref(false)
const showUpload = ref(false)
const tableData = ref([])
const total = ref(0)
const projects = ref([])
const uploadRef = ref(null)

const typeMap = { cost: '成本数据', quantity: '工程量', price: '材料价格' }
const importStatusMap = {
  pending: { text: '待处理', type: 'info' },
  processing: { text: '处理中', type: 'warning' },
  success: { text: '成功', type: 'success' },
  failed: { text: '失败', type: 'danger' }
}

const queryForm = reactive({ page: 1, size: 20 })
const uploadForm = reactive({ projectId: null, importType: 'cost', file: null })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getGlodonImportList(queryForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const onFileChange = (uploadFile) => { uploadForm.file = uploadFile.raw }
const onFileRemove = () => { uploadForm.file = null }

const handleImport = async () => {
  if (!uploadForm.projectId) { ElMessage.warning('请选择项目'); return }
  if (!uploadForm.file) { ElMessage.warning('请选择文件'); return }
  uploading.value = true
  try {
    const res = await importGlodonFile(uploadForm.projectId, uploadForm.importType, uploadForm.file)
    const result = res.data
    if (result?.status === 'success') {
      ElMessage.success(`导入成功，解析 ${result.success_rows || result.successRows} 行数据`)
    } else if (result?.status === 'failed') {
      ElMessage.error(`导入失败: ${result.error_msg || result.errorMsg}`)
    } else {
      ElMessage.success('导入完成')
    }
    showUpload.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '导入失败')
  } finally { uploading.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该导入记录？', '提示', { type: 'warning' })
  await deleteGlodonImport(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchData()
  getAllProjects().then(res => { projects.value = res.data || [] })
})
</script>
