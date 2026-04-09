<template>
  <div class="home-page">
    <!-- 欢迎栏 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div>
          <h2>{{ greeting }}，{{ userStore.realName || userStore.username || '用户' }}</h2>
          <p class="welcome-date">{{ currentDate }}</p>
        </div>
        <div class="todo-badge" v-if="homeData.todo_count > 0">
          <el-badge :value="homeData.todo_count" type="danger">
            <el-button type="primary" plain>待办事项</el-button>
          </el-badge>
        </div>
      </div>
    </el-card>

    <!-- 快捷入口 -->
    <el-card class="shortcut-card" v-if="homeData.shortcuts && homeData.shortcuts.length">
      <template #header>
        <span>快捷入口</span>
      </template>
      <div class="shortcut-grid">
        <div
          class="shortcut-item"
          v-for="item in homeData.shortcuts"
          :key="item.func_code"
        >
          <el-icon size="28"><component :is="item.func_icon || 'Grid'" /></el-icon>
          <span>{{ item.func_name }}</span>
        </div>
      </div>
    </el-card>

    <!-- 公告 -->
    <el-card class="announcement-card">
      <template #header>
        <span>最新公告</span>
      </template>
      <div v-if="homeData.announcements && homeData.announcements.length">
        <div
          class="announcement-item"
          v-for="item in homeData.announcements"
          :key="item.id"
        >
          <span class="announcement-title">{{ item.title }}</span>
          <span class="announcement-time">{{ item.publish_time }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无公告" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { getHomeData } from '@/api/auth'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
dayjs.locale('zh-cn')

const userStore = useUserStore()

const homeData = reactive({
  todo_count: 0,
  announcements: [],
  shortcuts: []
})

const currentDate = computed(() => dayjs().format('YYYY年MM月DD日 dddd'))

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

let pollTimer = null

const fetchHome = async () => {
  try {
    const res = await getHomeData()
    Object.assign(homeData, res.data)
  } catch (e) {
    // error handled by interceptor
  }
}

onMounted(() => {
  fetchHome()
  // V3.2: 每5分钟轮询待办数量
  pollTimer = setInterval(fetchHome, 5 * 60 * 1000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped lang="scss">
.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.welcome-card {
  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  h2 {
    font-size: 20px;
    color: #303133;
    margin-bottom: 8px;
  }

  .welcome-date {
    color: #909399;
    font-size: 14px;
  }
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  color: #606266;

  &:hover {
    background: #f5f7fa;
    color: #409eff;
  }

  span {
    font-size: 13px;
  }
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.announcement-title {
  color: #303133;
  cursor: pointer;

  &:hover {
    color: #409eff;
  }
}

.announcement-time {
  color: #909399;
  font-size: 13px;
}
</style>
