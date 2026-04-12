import { createApp, defineAsyncComponent } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { permissionDirective } from './directives/permission'
import './styles/global.scss'

// 高频全局组件（同步加载）
import StatusTag from './components/StatusTag.vue'
import PageHeader from './components/PageHeader.vue'
import SearchForm from './components/SearchForm.vue'
import DictTag from './components/DictTag.vue'

const app = createApp(App)

// 注册常用 Element Plus 图标（按需，仅注册侧边栏/按钮使用的）
const usedIcons = [
  'HomeFilled', 'List', 'User', 'Folder', 'Van', 'Star', 'Box', 'Tickets',
  'ShoppingCart', 'House', 'SetUp', 'DataLine', 'Money', 'Avatar', 'Finished',
  'Stamp', 'TrendCharts', 'DocumentCopy', 'Phone', 'FolderOpened', 'Setting',
  'Key', 'OfficeBuilding', 'Bell', 'Document', 'Tools', 'Switch', 'Collection',
  'Cpu', 'MagicStick', 'Share', 'DataAnalysis', 'ArrowLeft', 'Lock',
  'Plus', 'Edit', 'Delete', 'Search', 'Refresh', 'Close', 'Check',
  'Upload', 'Download', 'Warning', 'InfoFilled', 'SuccessFilled', 'CircleClose',
  'ArrowRight', 'ArrowDown', 'ArrowUp', 'More', 'View', 'Hide'
]
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  if (usedIcons.includes(key)) {
    app.component(key, component)
  }
}

// 高频全局组件
const syncComponents = { StatusTag, PageHeader, SearchForm, DictTag }
Object.entries(syncComponents).forEach(([name, comp]) => {
  app.component(name, comp)
})

// 低频业务组件（异步加载，首屏不需要）
const asyncComponents = {
  MoneyInput: () => import('./components/MoneyInput.vue'),
  QuantityInput: () => import('./components/QuantityInput.vue'),
  MoneyText: () => import('./components/MoneyText.vue'),
  UserPicker: () => import('./components/UserPicker.vue'),
  DeptPicker: () => import('./components/DeptPicker.vue'),
  ProjectSelect: () => import('./components/ProjectSelect.vue'),
  ContractSelect: () => import('./components/ContractSelect.vue'),
  SupplierSelect: () => import('./components/SupplierSelect.vue'),
  MaterialSelect: () => import('./components/MaterialSelect.vue'),
  FileUpload: () => import('./components/FileUpload.vue'),
  ApprovalFlow: () => import('./components/ApprovalFlow.vue'),
  ApprovalTimeline: () => import('./components/ApprovalTimeline.vue'),
  NumberingDisplay: () => import('./components/NumberingDisplay.vue')
}
Object.entries(asyncComponents).forEach(([name, loader]) => {
  app.component(name, defineAsyncComponent(loader))
})

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.directive('permission', permissionDirective)
app.mount('#app')
