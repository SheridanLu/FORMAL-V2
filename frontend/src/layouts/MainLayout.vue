<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">MOCHU-OA</span>
        <span v-else>M</span>
      </div>
      <div class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          :unique-opened="true"
        >
          <sidebar-menu :menus="menuTree" />
        </el-menu>
      </div>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 全屏切换 -->
          <el-tooltip content="全屏切换">
            <el-icon class="header-action" @click="toggleFullscreen">
              <FullScreen />
            </el-icon>
          </el-tooltip>
          <!-- 用户菜单 -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="User" />
              <span class="username">{{ userStore.realName || userStore.username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- TagsView -->
      <div class="tags-view" v-if="visitedTags.length">
        <el-tag
          v-for="tag in visitedTags"
          :key="tag.path"
          :closable="!tag.affix"
          :type="isActive(tag) ? '' : 'info'"
          :effect="isActive(tag) ? 'dark' : 'plain'"
          class="tag-item"
          @click="$router.push(tag.path)"
          @close="closeTag(tag)"
        >
          {{ tag.title }}
        </el-tag>
      </div>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <keep-alive :include="cachedViews">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { useWatermark } from '@/composables/useWatermark'
import { isMobile } from '@/config/mobile-features'
import SidebarMenu from './components/SidebarMenu.vue'
import { Fold, Expand, FullScreen } from '@element-plus/icons-vue'

const userStore = useUserStore()
const permissionStore = usePermissionStore()
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

// 菜单树从 permission store 获取
const menuTree = computed(() => permissionStore.menuTree)

// 当前激活菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta?.activeMenu) return meta.activeMenu
  return path
})

// ===== 面包屑 =====
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(r => r.meta?.title && !r.meta?.hidden)
  const crumbs = []
  if (matched[0]?.path !== '/home') {
    crumbs.push({ path: '/home', title: '首页' })
  }
  matched.forEach(r => {
    crumbs.push({ path: r.path, title: r.meta.title })
  })
  return crumbs
})

// ===== TagsView =====
const visitedTags = ref([{ path: '/home', title: '首页', affix: true }])
const cachedViews = computed(() => visitedTags.value.map(t => t.name).filter(Boolean))

function isActive(tag) {
  return tag.path === route.path
}

function addTag(r) {
  if (!r.meta?.title || r.meta.hidden) return
  if (visitedTags.value.some(t => t.path === r.path)) return
  visitedTags.value.push({
    path: r.path,
    title: r.meta.title,
    name: r.name,
    affix: r.meta.affix || false
  })
}

function closeTag(tag) {
  const idx = visitedTags.value.indexOf(tag)
  if (idx === -1) return
  visitedTags.value.splice(idx, 1)
  if (isActive(tag)) {
    // 跳转到前一个标签
    const last = visitedTags.value[idx - 1] || visitedTags.value[visitedTags.value.length - 1]
    if (last) router.push(last.path)
  }
}

watch(route, (r) => addTag(r), { immediate: true })

// ===== 全屏 =====
function toggleFullscreen() {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    document.documentElement.requestFullscreen()
  }
}

// ===== 全局水印 =====
const { setWatermark } = useWatermark()
onMounted(() => {
  const name = userStore.realName || userStore.username || '用户'
  setWatermark(name)
  // 移动端自动折叠侧边栏
  if (isMobile()) {
    isCollapse.value = true
  }
})

// ===== 用户操作 =====
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'password') {
    router.push('/profile?tab=password')
  }
}
</script>

<style scoped lang="scss">
.main-layout {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;

  .el-menu {
    border-right: none;
  }
}

:deep(.el-menu--vertical.el-menu--collapse .el-sub-menu .el-menu--inline) {
  display: none !important;
}
:deep(.el-menu--vertical:not(.el-menu--collapse) .el-sub-menu .el-menu--inline) {
  display: block;
}

.menu-scroll {
  flex: 1;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-width: thin;

  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.2); border-radius: 2px; }

  .el-sub-menu .el-menu {
    background-color: #1f2d3d !important;
  }
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  font-size: 18px;
  cursor: pointer;
  color: #606266;
  &:hover { color: #409eff; }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}

.tags-view {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 4px 12px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;

  .tag-item {
    cursor: pointer;
    user-select: none;
  }
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}

// ===== 移动端适配 =====
@media screen and (max-width: 768px) {
  .sidebar {
    position: fixed;
    z-index: 1000;
    height: 100%;
    transition: transform 0.3s;
  }

  .header {
    padding: 0 12px;
    height: 50px;
  }

  .breadcrumb {
    display: none;
  }

  .username {
    display: none;
  }

  .tags-view {
    display: none;
  }

  .main-content {
    padding: 12px;
  }
}
</style>
