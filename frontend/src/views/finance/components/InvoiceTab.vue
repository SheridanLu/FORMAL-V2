<template>
  <div>
    <div class="toolbar">
      <el-button v-permission="'finance:invoice-manage'" type="primary" @click="openCreate">新建发票</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="invoice_no" label="发票编号" width="160" />
      <el-table-column label="发票类型" width="120">
        <template #default="{ row }">{{ getInvoiceTypeLabel(row.invoice_type) }}</template>
      </el-table-column>
      <el-table-column label="金额" width="130" align="right">
        <template #default="{ row }"><money-text :value="row.amount" /></template>
      </el-table-column>
      <el-table-column prop="invoice_date" label="开票日期" width="110" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><status-tag :status="row.status" /></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-permission="'finance:invoice-manage'" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-permission="'finance:invoice-manage'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑发票' : '新建发票'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发票号码" prop="invoiceNo">
              <el-input v-model="form.invoiceNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票类型" prop="invoiceType">
              <el-select v-model="form.invoiceType" style="width: 100%">
                <el-option v-for="opt in invoiceTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="金额" prop="amount"><money-input v-model="form.amount" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="税率(%)">
              <el-input-number v-model="form.taxRate" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="税额">
              <money-input v-model="form.taxAmount" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="开票日期" prop="invoiceDate">
              <el-date-picker v-model="form.invoiceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务类型" prop="bizType">
              <el-select v-model="form.bizType" style="width: 100%">
                <el-option label="结算" value="statement" />
                <el-option label="付款" value="payment" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务单据ID" prop="bizId">
              <el-input-number v-model="form.bizId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开票方">
          <el-input v-model="form.invoiceParty" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit(onSuccess)">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getInvoiceList, createInvoice, updateInvoice, deleteInvoice } from '@/api/finance'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useDict } from '@/composables/useDict'

const { loading, tableData, total, query, fetchData, handleSizeChange, handleCurrentChange } = useTable(getInvoiceList)
const { formRef, submitting, formVisible, isEdit, form, openCreate, openEdit, handleSubmit } = useForm(createInvoice, updateInvoice, {
  invoiceNo: '', invoiceType: '', amount: null, taxRate: null, taxAmount: null,
  invoiceDate: null, bizType: '', bizId: null, invoiceParty: ''
})

const { options: invoiceTypeOptions, getLabel: getInvoiceTypeLabel } = useDict('invoice_type')

const rules = {
  invoiceNo: [{ required: true, message: '请输入发票号码', trigger: 'blur' }],
  invoiceType: [{ required: true, message: '请选择发票类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  invoiceDate: [{ required: true, message: '请选择开票日期', trigger: 'change' }],
  bizType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  bizId: [{ required: true, message: '请输入业务单据ID', trigger: 'blur' }]
}

const onSuccess = () => fetchData()

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await deleteInvoice(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

fetchData()
</script>
