package com.mochu.bpm.controller;

import com.mochu.bpm.dto.ProcessDeployDTO;
import com.mochu.bpm.service.BpmProcessDefinitionService;
import com.mochu.bpm.vo.ProcessDefinitionVO;
import com.mochu.common.result.R;
import com.mochu.framework.annotation.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bpm/process-def")
@RequiredArgsConstructor
public class BpmProcessDefinitionController {

    private final BpmProcessDefinitionService processDefService;

    @GetMapping
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<List<ProcessDefinitionVO>> list(@RequestParam(required = false) String keyword) {
        return R.ok(processDefService.listProcessDefinitions(keyword));
    }

    @AuditLog(operateType = "CREATE", operateModule = "流程管理", bizType = "process_definition")
    @PostMapping("/deploy")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<String> deploy(@Valid @RequestBody ProcessDeployDTO dto) {
        return R.ok(processDefService.deployProcess(dto));
    }

    @GetMapping("/{processDefId}/xml")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<String> getBpmnXml(@PathVariable String processDefId) {
        return R.ok(processDefService.getProcessBpmnXml(processDefId));
    }

    @AuditLog(operateType = "STATUS_CHANGE", operateModule = "流程管理", bizType = "process_definition")
    @PutMapping("/{processDefId}/suspend")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<Void> suspend(@PathVariable String processDefId) {
        processDefService.suspendOrActivate(processDefId, true);
        return R.ok();
    }

    @AuditLog(operateType = "STATUS_CHANGE", operateModule = "流程管理", bizType = "process_definition")
    @PutMapping("/{processDefId}/activate")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<Void> activate(@PathVariable String processDefId) {
        processDefService.suspendOrActivate(processDefId, false);
        return R.ok();
    }

    @AuditLog(operateType = "DELETE", operateModule = "流程管理", bizType = "process_definition", saveBefore = true)
    @DeleteMapping("/{processDefId}")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<Void> delete(@PathVariable String processDefId) {
        processDefService.deleteDeployment(processDefId);
        return R.ok();
    }

    @AuditLog(operateType = "UPDATE", operateModule = "流程管理", bizType = "process_definition", saveBefore = true)
    @PutMapping("/{processDefId}/ext")
    @PreAuthorize("hasAuthority('bpm:process-manage')")
    public R<Void> updateExt(@PathVariable String processDefId, @Valid @RequestBody ProcessDeployDTO dto) {
        processDefService.updateExt(processDefId, dto);
        return R.ok();
    }
}
