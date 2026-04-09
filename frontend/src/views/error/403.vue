<template>
  <div class="error-page">
    <div class="error-content">
      <h1>403</h1>
      <h2>抱歉，您没有权限访问此页面</h2>
      <p>请联系管理员获取相关权限</p>
      <el-button type="primary" @click="$router.push('/home')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f5f7fa;
}
.error-content {
  text-align: center;
  h1 { font-size: 120px; color: #409eff; margin: 0; line-height: 1; }
  h2 { font-size: 24px; color: #303133; margin: 20px 0 10px; }
  p { font-size: 14px; color: #909399; margin-bottom: 30px; }
}
</style>
