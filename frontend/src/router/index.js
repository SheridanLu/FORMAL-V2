import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import staticRoutes from './static-routes'
import asyncRoutes from './async-routes'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

NProgress.configure({ showSpinner: false })

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...staticRoutes,
    // 兜底 404 — 必须在动态路由注入之后才有意义，先放一个占位
    // 真正的 catch-all 在 generateRoutes 后动态添加
  ]
})

// 白名单路径 — 不需要登录
const whiteList = ['/login', '/403', '/404']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || 'MOCHU-OA'} - 施工管理系统`

  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  // 已登录
  if (userStore.isLoggedIn) {
    if (to.path === '/login') {
      next('/home')
      return
    }

    // 路由还没注入 → 先拉用户信息 + 生成路由
    if (!permissionStore.routesAdded) {
      try {
        // 确保用户信息已拉取
        if (!userStore.userInfo) {
          await userStore.fetchUserInfo()
        }

        // 注入异步路由模块
        permissionStore.setAsyncRoutes(asyncRoutes)
        const accessRoutes = permissionStore.generateRoutes()

        // 动态添加路由
        accessRoutes.forEach(route => {
          router.addRoute(route)
        })

        // 添加 catch-all 404（必须在所有动态路由之后）
        router.addRoute({
          path: '/:pathMatch(.*)*',
          redirect: '/404'
        })

        permissionStore.setRoutesAdded(true)

        // 用 replace 重新导航到目标路由，使新增的路由生效
        next({ ...to, replace: true })
        return
      } catch (e) {
        userStore.resetState()
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
        return
      }
    }

    next()
    return
  }

  // 未登录
  if (whiteList.includes(to.path)) {
    next()
  } else {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
