<template>
  <div>
    <div style="margin-bottom: 12px">
      <el-button type="primary" v-permission="['doc:upload','doc:download','doc:manage']" @click="openCreate">上传竣工图纸</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="drawing_no" label="图纸编号" width="140" />
      <el-table-column prop="project_name" label="项目名称" min-width="130" show-overflow-tooltip />
      <el-table-column prop="drawing_name" label="图纸名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="drawing_type" label="图纸类型" width="110" />
      <el-table-column prop="version" label="版本" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handlePreview(row)">预览</el-button>
          <el-button type="danger" link size="small" v-permission="['doc:upload','doc:download','doc:manage']" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      style="margin-top: 16px; justify-content: flex-end"
      background
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.page"
      @current-change="handleCurrentChange"
    />

    <!-- Dialog -->
    <el-dialog v-model="formVisible" title="上传竣工图纸" width="650px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="关联项目" prop="projectId">
              <project-select v-model="form.projectId" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图纸名称" prop="drawingName">
              <el-input v-model="form.drawingName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="图纸类型" prop="drawingType">
              <el-input v-model="form.drawingType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本">
              <el-input v-model="form.version" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="文件地址" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="上传后自动填入" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit(fetchData)">确定</el-button>
      </template>
    </el-dialog>

    <!-- DWG/图纸预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="'图纸预览 - ' + previewTitle" width="90%" top="2vh" destroy-on-close>
      <div v-if="previewUrl" style="height: 75vh">
        <iframe v-if="isImageOrPdf" :src="previewUrl" style="width: 100%; height: 100%; border: none" />
        <div v-else style="text-align: center; padding: 60px">
          <el-icon :size="64" color="#909399"><Document /></el-icon>
          <p style="margin-top: 16px; color: #909399">
            DWG/CAD 文件需要转换后才能在线预览。
          </p>
          <p style="color: #909399; font-size: 13px">支持格式：PDF、PNG、JPG 可直接预览</p>
          <el-button type="primary" style="margin-top: 16px" @click="downloadFile">下载原始文件</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getDrawingList, uploadDrawing, deleteDrawing } from '@/api/completion'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'

const { loading, tableData, total, query, fetchData, handleCurrentChange } = useTable(getDrawingList)

const defaultForm = {
  projectId: null,
  drawingName: '',
  drawingType: '',
  fileUrl: '',
  version: '',
  remark: ''
}

const { formRef, submitting, formVisible, form, openCreate, handleSubmit } =
  useForm(uploadDrawing, null, defaultForm)

const rules = {
  projectId: [{ required: true, message: '请选择关联项目', trigger: 'change' }],
  drawingName: [{ required: true, message: '请输入图纸名称', trigger: 'blur' }],
  drawingType: [{ required: true, message: '请输入图纸类型', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请上传文件', trigger: 'blur' }]
}

// ===== DWG/图纸预览 =====
const previewVisible = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')
const previewFileUrl = ref('')

const isImageOrPdf = computed(() => {
  const url = previewUrl.value.toLowerCase()
  return url.endsWith('.pdf') || url.endsWith('.png') || url.endsWith('.jpg') || url.endsWith('.jpeg')
})

function handlePreview(row) {
  previewTitle.value = row.drawing_name || '图纸'
  previewFileUrl.value = row.file_url || ''
  previewUrl.value = row.file_url || ''
  previewVisible.value = true
}

function downloadFile() {
  if (previewFileUrl.value) {
    window.open(previewFileUrl.value, '_blank')
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteDrawing(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>
