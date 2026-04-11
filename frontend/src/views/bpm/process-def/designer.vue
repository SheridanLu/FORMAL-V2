<template>
  <el-dialog v-model="visible" :title="isView ? '查看流程图' : '流程设计器'" width="90vw" fullscreen destroy-on-close>
    <div style="display: flex; flex-direction: column; height: calc(100vh - 120px);">
      <!-- 工具栏 -->
      <div style="padding: 8px 0; display: flex; gap: 8px; border-bottom: 1px solid #eee; margin-bottom: 8px;">
        <el-input v-if="!isView" v-model="deployForm.processDefKey" placeholder="流程Key（唯一标识）" style="width: 220px;" />
        <el-input v-if="!isView" v-model="deployForm.processName" placeholder="流程名称" style="width: 200px;" />
        <el-button v-if="!isView" type="primary" :loading="deploying" @click="handleDeploy">部署流程</el-button>
        <el-button @click="handleDownloadXml">下载XML</el-button>
        <el-button v-if="!isView" @click="loadDefaultXml">加载默认模板</el-button>
      </div>

      <!-- BPMN 容器 -->
      <div ref="bpmnContainer" style="flex: 1; border: 1px solid #e4e7ed; border-radius: 4px; background: #fafafa;" />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { deployProcess, getProcessBpmnXml } from '@/api/bpm'

const emit = defineEmits(['deployed'])
const visible = ref(false)
const isView = ref(false)
const deploying = ref(false)
const bpmnContainer = ref(null)
const deployForm = reactive({ processDefKey: '', processName: '' })

let bpmnModeler = null

const DEFAULT_XML = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:flowable="http://flowable.org/bpmn"
             targetNamespace="http://www.flowable.org/processdef">
  <process id="process_id" name="新流程" isExecutable="true">
    <startEvent id="start" name="开始" />
    <userTask id="task1" name="审批" flowable:assignee="\${initiatorId}" />
    <endEvent id="end" name="结束" />
    <sequenceFlow id="flow1" sourceRef="start" targetRef="task1" />
    <sequenceFlow id="flow2" sourceRef="task1" targetRef="end" />
  </process>
</definitions>`

async function open(row) {
  visible.value = true
  isView.value = !!row
  if (row) {
    deployForm.processDefKey = row.key || ''
    deployForm.processName = row.name || ''
  } else {
    deployForm.processDefKey = ''
    deployForm.processName = ''
  }

  await nextTick()
  await initModeler()

  if (row?.id) {
    try {
      const res = await getProcessBpmnXml(row.id)
      await importXml(res.data)
    } catch (e) {
      ElMessage.warning('加载流程图失败，显示默认模板')
      await importXml(DEFAULT_XML)
    }
  } else {
    await importXml(DEFAULT_XML)
  }
}

async function initModeler() {
  if (bpmnModeler) {
    bpmnModeler.destroy()
    bpmnModeler = null
  }
  try {
    // 动态导入 bpmn-js（需要 npm install bpmn-js）
    const BpmnModeler = (await import('bpmn-js/lib/Modeler')).default
    bpmnModeler = new BpmnModeler({
      container: bpmnContainer.value,
      keyboard: { bindTo: window }
    })
    if (isView.value) {
      // 只读模式：替换为 Viewer
      bpmnModeler.destroy()
      const BpmnViewer = (await import('bpmn-js/lib/NavigatedViewer')).default
      bpmnModeler = new BpmnViewer({ container: bpmnContainer.value })
    }
  } catch (e) {
    console.warn('bpmn-js 未安装，请执行: npm install bpmn-js')
    ElMessage.warning('bpmn-js 未安装，无法显示流程图。请执行: npm install bpmn-js')
  }
}

async function importXml(xml) {
  if (!bpmnModeler) return
  try {
    await bpmnModeler.importXML(xml)
    bpmnModeler.get('canvas').zoom('fit-viewport')
  } catch (e) {
    ElMessage.error('流程图解析失败：' + e.message)
  }
}

function loadDefaultXml() {
  importXml(DEFAULT_XML)
}

async function handleDeploy() {
  if (!deployForm.processDefKey) {
    ElMessage.warning('请输入流程Key')
    return
  }
  const xml = await getXml()
  if (!xml) return
  deploying.value = true
  try {
    await deployProcess({
      processDefKey: deployForm.processDefKey,
      processName: deployForm.processName,
      bpmn20Xml: xml
    })
    ElMessage.success('流程部署成功')
    visible.value = false
    emit('deployed')
  } finally {
    deploying.value = false
  }
}

async function handleDownloadXml() {
  const xml = await getXml()
  if (!xml) return
  const blob = new Blob([xml], { type: 'text/xml' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = (deployForm.processDefKey || 'process') + '.bpmn20.xml'
  a.click()
}

async function getXml() {
  if (!bpmnModeler) return null
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    return xml
  } catch (e) {
    ElMessage.error('获取流程XML失败')
    return null
  }
}

defineExpose({ open })
</script>
