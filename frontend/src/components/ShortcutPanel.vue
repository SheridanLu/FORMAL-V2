<template>
  <el-card shadow="never" class="shortcut-panel">
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>常用功能</span>
        <el-button type="primary" link size="small" @click="configVisible = true">
          配置
        </el-button>
      </div>
    </template>

    <div class="shortcut-grid">
      <div v-for="item in shortcuts" :key="item.menu_code"
           class="shortcut-item" @click="goTo(item)">
        <el-icon :size="28" color="var(--el-color-primary)">
          <component :is="item.menu_icon || 'Document'" />
        </el-icon>
        <span class="shortcut-label">{{ item.menu_name }}</span>
      </div>
      <div v-if="shortcuts.length === 0" class="shortcut-empty">
        暂无快捷入口，点击"配置"添加
      </div>
    </div>

    <!-- 配置对话框 -->
    <el-dialog v-model="configVisible" title="配置快捷入口" width="500px">
      <div style="margin-bottom: 8px; color: #999">最多配置 8 个快捷入口</div>
      <el-checkbox-group v-model="selected" :max="8">
        <el-checkbox v-for="menu in availableMenus" :key="menu.code"
                     :label="menu.code" :value="menu.code">
          {{ menu.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="configVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserShortcuts, updateUserShortcuts, getAvailableMenus } from '@/api/userShortcut'

const router = useRouter()
const shortcuts = ref([])
const availableMenus = ref([])
const selected = ref([])
const configVisible = ref(false)
const saving = ref(false)

const fetchShortcuts = async () => {
  const res = await getUserShortcuts()
  shortcuts.value = res.data || []
  selected.value = shortcuts.value.map(s => s.menu_code)
}

const fetchMenus = async () => {
  const res = await getAvailableMenus()
  availableMenus.value = res.data || []
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateUserShortcuts({ menuCodes: selected.value })
    ElMessage.success('配置已保存')
    configVisible.value = false
    fetchShortcuts()
  } finally {
    saving.value = false
  }
}

const goTo = (item) => {
  if (item.menu_path) router.push(item.menu_path)
}

onMounted(() => {
  fetchShortcuts()
  fetchMenus()
})
</script>

<style scoped>
.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}
.shortcut-item:hover { background-color: var(--el-fill-color-light); }
.shortcut-label { margin-top: 8px; font-size: 13px; color: #333; }
.shortcut-empty { grid-column: span 4; text-align: center; color: #999; padding: 20px; }
</style>
