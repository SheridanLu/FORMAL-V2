<template>
  <div class="equipment-page">
    <!-- 搜索 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="设备名称">
          <el-input v-model="queryForm.equipmentName" placeholder="请输入" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.category" placeholder="全部" clearable style="width: 120px">
            <el-option label="自有" value="owned" />
            <el-option label="租赁" value="rented" />
            <el-option label="分包" value="subcontracted" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="闲置" value="idle" />
            <el-option label="使用中" value="in_use" />
            <el-option label="维修中" value="maintenance" />
            <el-option label="已报废" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" style="margin-top: 12px">
      <div style="margin-bottom: 12px">
        <el-button type="primary" @click="handleAdd" v-permission="'equipment:manage'">新增设备</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="equipment_no" label="设备编号" width="120" />
        <el-table-column prop="equipment_name" label="设备名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="model" label="规格型号" width="130" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="80">
          <template #default="{ row }">
            <el-tag :type="categoryMap[row.category]?.type" size="small">{{ categoryMap[row.category]?.text || row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="130" show-overflow-tooltip />
        <el-table-column prop="next_maintenance_date" label="下次保养" width="110" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'equipment:manage'">编辑</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(row, cmd)" style="margin-left: 8px" v-permission="'equipment:manage'">
              <el-button type="warning" link size="small">状态<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="idle">闲置</el-dropdown-item>
                  <el-dropdown-item command="in_use">使用中</el-dropdown-item>
                  <el-dropdown-item command="maintenance">维修中</el-dropdown-item>
                  <el-dropdown-item command="scrapped">已报废</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-if="row.status !== 'in_use'" v-permission="'equipment:manage'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total > 0" style="margin-top: 16px; justify-content: flex-end" background
        layout="total, sizes, prev, pager, next, jumper" :total="total" :page-sizes="[10, 20, 50]"
        v-model:current-page="queryForm.page" v-model:page-size="queryForm.size"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备' : '新增设备'" width="750px" @closed="resetForm" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备名称" prop="equipmentName">
              <el-input v-model="form.equipmentName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="form.model" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="分类">
              <el-select v-model="form.category" style="width: 100%">
                <el-option label="自有" value="owned" />
                <el-option label="租赁" value="rented" />
                <el-option label="分包" value="subcontracted" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="品牌">
              <el-input v-model="form.brand" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出厂编号">
              <el-input v-model="form.serialNo" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="台/套/辆" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购置日期">
              <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购置价格">
              <el-input-number v-model="form.purchasePrice" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="存放位置">
              <el-input v-model="form.location" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次保养">
              <el-date-picker v-model="form.nextMaintenanceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getEquipmentList, createEquipment, updateEquipment, updateEquipmentStatus, deleteEquipment } from '@/api/equipment'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const categoryMap = {
  owned: { text: '自有', type: '' },
  rented: { text: '租赁', type: 'warning' },
  subcontracted: { text: '分包', type: 'info' }
}

const statusMap = {
  idle: { text: '闲置', type: 'info' },
  in_use: { text: '使用中', type: 'success' },
  maintenance: { text: '维修中', type: 'warning' },
  scrapped: { text: '已报废', type: 'danger' }
}

const queryForm = reactive({ equipmentName: '', category: '', status: '', page: 1, size: 20 })

const form = reactive({
  equipmentName: '', model: '', category: 'owned', brand: '', serialNo: '',
  unit: '', purchaseDate: null, purchasePrice: null, location: '',
  nextMaintenanceDate: null, remark: ''
})

const rules = {
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getEquipmentList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { queryForm.page = 1; fetchData() }
const handleReset = () => {
  queryForm.equipmentName = ''; queryForm.category = ''; queryForm.status = ''; queryForm.page = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false; editId.value = null; dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, {
    equipmentName: row.equipment_name, model: row.model || '', category: row.category || 'owned',
    brand: row.brand || '', serialNo: row.serial_no || '', unit: row.unit || '',
    purchaseDate: row.purchase_date || null, purchasePrice: row.purchase_price,
    location: row.location || '', nextMaintenanceDate: row.next_maintenance_date || null,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, {
    equipmentName: '', model: '', category: 'owned', brand: '', serialNo: '',
    unit: '', purchaseDate: null, purchasePrice: null, location: '',
    nextMaintenanceDate: null, remark: ''
  })
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateEquipment(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createEquipment(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false; fetchData()
  } finally { submitting.value = false }
}

const handleStatusChange = async (row, status) => {
  await ElMessageBox.confirm(`确定将设备状态改为"${statusMap[status]?.text}"？`, '提示', { type: 'warning' })
  await updateEquipmentStatus(row.id, status)
  ElMessage.success('状态已更新')
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除设备"${row.equipment_name}"？`, '提示', { type: 'warning' })
  await deleteEquipment(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.search-card { margin-bottom: 0; }
</style>
