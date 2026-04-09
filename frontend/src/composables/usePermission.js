import { usePermissionStore } from '@/stores/permission'

/**
 * 权限检查 composable
 */
export function usePermission() {
  const permissionStore = usePermissionStore()

  function hasPermission(code) {
    return permissionStore.hasPermission(code)
  }

  function hasAnyPermission(codes) {
    return permissionStore.hasAnyPermission(codes)
  }

  function hasAllPermissions(codes) {
    return permissionStore.hasAllPermissions(codes)
  }

  return {
    hasPermission,
    hasAnyPermission,
    hasAllPermissions
  }
}
