package com.mochu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.common.exception.BusinessException;
import com.mochu.system.dto.DeptDTO;
import com.mochu.system.entity.SysDept;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysDeptMapper;
import com.mochu.system.mapper.SysUserMapper;
import com.mochu.system.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务 — 对照 V3.2 部门管理
 */
@Service
@RequiredArgsConstructor
public class DeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 查询部门树
     */
    public List<DeptVO> getDeptTree() {
        List<SysDept> depts = sysDeptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().orderByAsc(SysDept::getSort)
        );
        List<DeptVO> voList = depts.stream().map(this::toVO).collect(Collectors.toList());
        return buildTree(voList);
    }

    /**
     * 部门详情
     */
    public DeptVO getDeptById(Integer id) {
        SysDept dept = sysDeptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        return toVO(dept);
    }

    /**
     * 创建部门
     */
    public Integer createDept(DeptDTO dto) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);

        // 计算 level 和 path
        if (dto.getParentId() == 0) {
            dept.setLevel(1);
            dept.setPath("/");
        } else {
            SysDept parent = sysDeptMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("上级部门不存在");
            }
            dept.setLevel(parent.getLevel() + 1);
            dept.setPath(parent.getPath());
        }

        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }

        sysDeptMapper.insert(dept);

        // 更新 path（包含自身 id）
        String fullPath;
        if (dto.getParentId() == 0) {
            fullPath = "/" + dept.getId() + "/";
        } else {
            SysDept parent = sysDeptMapper.selectById(dto.getParentId());
            fullPath = parent.getPath() + dept.getId() + "/";
        }
        dept.setPath(fullPath);
        sysDeptMapper.updateById(dept);

        return dept.getId();
    }

    /**
     * 更新部门
     */
    public void updateDept(DeptDTO dto) {
        SysDept dept = sysDeptMapper.selectById(dto.getId());
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        dept.setName(dto.getName());
        dept.setSort(dto.getSort());
        dept.setLeaderId(dto.getLeaderId());
        dept.setPhone(dto.getPhone());
        dept.setRemark(dto.getRemark());
        if (dto.getStatus() != null) {
            dept.setStatus(dto.getStatus());
        }

        sysDeptMapper.updateById(dept);
    }

    /**
     * 启用/停用部门 — V3.2 §5.9.6
     */
    public void updateDeptStatus(Integer id, Integer status) {
        SysDept dept = sysDeptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        dept.setStatus(status);
        sysDeptMapper.updateById(dept);
    }

    /**
     * 删除部门（逻辑删除）
     */
    public void deleteDept(Integer id) {
        // 检查是否有子部门
        Long childCount = sysDeptMapper.selectCount(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("存在子部门，无法删除");
        }

        // 检查是否有用户
        Long userCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("部门下存在用户，无法删除");
        }

        sysDeptMapper.deleteById(id);
    }

    private DeptVO toVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        BeanUtils.copyProperties(dept, vo);
        // 查询负责人姓名
        if (dept.getLeaderId() != null) {
            SysUser leader = sysUserMapper.selectById(dept.getLeaderId());
            if (leader != null) {
                vo.setLeaderName(leader.getRealName());
            }
        }
        return vo;
    }

    private List<DeptVO> buildTree(List<DeptVO> voList) {
        Map<Integer, List<DeptVO>> parentMap = voList.stream()
                .collect(Collectors.groupingBy(DeptVO::getParentId));

        voList.forEach(vo -> vo.setChildren(parentMap.getOrDefault(vo.getId(), new ArrayList<>())));

        return voList.stream()
                .filter(vo -> vo.getParentId() == 0)
                .collect(Collectors.toList());
    }
}
