<template>
  <div class="profile-page">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="/api/v1/attachments/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            accept="image/*"
          >
            <el-avatar :size="80" :src="userStore.userInfo?.avatar_url" icon="User" />
            <div class="avatar-mask">更换头像</div>
          </el-upload>
        </div>
        <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px" style="max-width:500px;margin:20px auto 0">
          <el-form-item label="用户名">
            <el-input :model-value="userStore.userInfo?.username" disabled />
          </el-form-item>
          <el-form-item label="姓名" prop="real_name">
            <el-input v-model="infoForm.real_name" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="infoForm.email" />
          </el-form-item>
          <el-form-item label="部门">
            <el-input :model-value="userStore.userInfo?.dept_name" disabled />
          </el-form-item>
          <el-form-item label="职位">
            <el-input :model-value="userStore.userInfo?.position" disabled />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingInfo" @click="saveInfo">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width:500px;margin:20px auto 0">
          <el-form-item label="当前密码" prop="old_password">
            <el-input v-model="pwdForm.old_password" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input v-model="pwdForm.new_password" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm_password">
            <el-input v-model="pwdForm.confirm_password" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingPwd" @click="changePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 我的待办 -->
      <el-tab-pane label="我的待办" name="todos">
        <el-table v-loading="todoLoading" :data="todoList" border>
          <el-table-column prop="title" label="待办事项" min-width="200" show-overflow-tooltip />
          <el-table-column prop="biz_type" label="类型" width="120" />
          <el-table-column prop="created_at" label="创建时间" width="170" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }"><status-tag :status="row.status" /></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { resetPassword } from '@/api/auth'
import { getTodoList } from '@/api/todo'
import request from '@/utils/request'

const route = useRoute()
const userStore = useUserStore()
const activeTab = ref(route.query.tab || 'info')

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}))

// ===== 基本信息 =====
const infoFormRef = ref(null)
const savingInfo = ref(false)
const infoForm = reactive({
  real_name: '',
  phone: '',
  email: ''
})
const infoRules = {
  real_name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

function initInfoForm() {
  const info = userStore.userInfo
  if (info) {
    infoForm.real_name = info.real_name || ''
    infoForm.phone = info.phone || ''
    infoForm.email = info.email || ''
  }
}

async function saveInfo() {
  await infoFormRef.value.validate()
  savingInfo.value = true
  try {
    await request.put('/api/v1/users/profile', infoForm)
    ElMessage.success('保存成功')
    await userStore.fetchUserInfo()
  } finally {
    savingInfo.value = false
  }
}

function handleAvatarSuccess(response) {
  if (response.code === 200 && response.data?.file_url) {
    request.put('/api/v1/users/profile', { avatar_url: response.data.file_url }).then(() => {
      userStore.fetchUserInfo()
      ElMessage.success('头像已更新')
    })
  }
}

// ===== 修改密码 =====
const pwdFormRef = ref(null)
const savingPwd = ref(false)
const pwdForm = reactive({ old_password: '', new_password: '', confirm_password: '' })
const pwdRules = {
  old_password: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  new_password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码至少8位', trigger: 'blur' }
  ],
  confirm_password: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.new_password) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

async function changePassword() {
  await pwdFormRef.value.validate()
  savingPwd.value = true
  try {
    await resetPassword({ old_password: pwdForm.old_password, new_password: pwdForm.new_password })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
  } finally {
    savingPwd.value = false
  }
}

// ===== 我的待办 =====
const todoLoading = ref(false)
const todoList = ref([])

async function fetchTodos() {
  todoLoading.value = true
  try {
    const res = await getTodoList({ page: 1, size: 50 })
    todoList.value = res.data?.records || []
  } finally {
    todoLoading.value = false
  }
}

onMounted(async () => {
  if (!userStore.userInfo) await userStore.fetchUserInfo()
  initInfoForm()
  fetchTodos()
})
</script>

<style scoped lang="scss">
.profile-page { max-width: 900px; }
.avatar-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
.avatar-uploader {
  position: relative;
  cursor: pointer;
  .avatar-mask {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0,0,0,0.5);
    color: #fff;
    text-align: center;
    font-size: 12px;
    padding: 4px 0;
    border-radius: 0 0 50% 50%;
    opacity: 0;
    transition: opacity 0.3s;
  }
  &:hover .avatar-mask { opacity: 1; }
}
</style>
