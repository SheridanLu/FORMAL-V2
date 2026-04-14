package com.mochu.system.service;

import com.mochu.common.constant.Constants;
import com.mochu.common.security.SecurityUtils;
import com.mochu.system.vo.AnnouncementVO;
import com.mochu.system.vo.HomeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 首页服务 — 对照 V3.2 §4.2, §5.9.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AnnouncementService announcementService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 获取首页数据
     */
    public HomeVO getHomeData() {
        Integer userId = SecurityUtils.getCurrentUserId();
        HomeVO vo = new HomeVO();

        // 待办数量（从 Redis 缓存读取，无缓存则返回 0）
        vo.setTodoCount(getTodoCount());

        // 最新公告（已发布，最多5条）
        try {
            List<AnnouncementVO> published = announcementService.listPublished(5);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<HomeVO.AnnouncementVO> annoList = published.stream().map(a -> {
                HomeVO.AnnouncementVO avo = new HomeVO.AnnouncementVO();
                avo.setId(a.getId());
                avo.setTitle(a.getTitle());
                avo.setPublishTime(a.getPublishTime() != null ? a.getPublishTime().format(fmt) : "");
                return avo;
            }).collect(Collectors.toList());
            vo.setAnnouncements(annoList);
        } catch (Exception e) {
            log.warn("加载首页公告失败: {}", e.getMessage());
            vo.setAnnouncements(new ArrayList<>());
        }

        // 快捷入口 — 暂返回默认列表
        List<HomeVO.ShortcutVO> shortcuts = new ArrayList<>();
        if (shortcuts.isEmpty()) {
            shortcuts = buildDefaultShortcuts();
        }
        vo.setShortcuts(shortcuts);

        // 工作台统计数据
        vo.setStats(buildStats(userId));

        return vo;
    }

    /**
     * 构建默认快捷入口
     */
    private List<HomeVO.ShortcutVO> buildDefaultShortcuts() {
        List<HomeVO.ShortcutVO> list = new ArrayList<>();
        list.add(createShortcut("project", "项目管理", "Folder", 1));
        list.add(createShortcut("contract", "合同管理", "Document", 2));
        list.add(createShortcut("approval", "审批中心", "Stamp", 3));
        list.add(createShortcut("finance", "财务管理", "Money", 4));
        list.add(createShortcut("inventory", "库存管理", "Box", 5));
        list.add(createShortcut("hr", "人事管理", "User", 6));
        return list;
    }

    private HomeVO.ShortcutVO createShortcut(String code, String name, String icon, int order) {
        HomeVO.ShortcutVO s = new HomeVO.ShortcutVO();
        s.setFuncCode(code);
        s.setFuncName(name);
        s.setFuncIcon(icon);
        s.setSortOrder(order);
        return s;
    }

    /**
     * 构建工作台统计数据 — V3.2 §4.2
     */
    private Map<String, Object> buildStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 我的项目数
            Integer projectCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM biz_project_member WHERE user_id = ?",
                Integer.class, userId);
            stats.put("projectCount", projectCount != null ? projectCount : 0);

            // 待审批数
            Integer pendingApprovalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM biz_approval_node WHERE handler_id = ? AND status = 'pending'",
                Integer.class, userId);
            stats.put("pendingApprovalCount", pendingApprovalCount != null ? pendingApprovalCount : 0);

            // 本月合同数
            Integer contractCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM biz_contract WHERE MONTH(created_at) = MONTH(NOW()) AND YEAR(created_at) = YEAR(NOW())",
                Integer.class);
            stats.put("contractCount", contractCount != null ? contractCount : 0);

            // 待办总数 (from Redis)
            stats.put("todoCount", getTodoCount());
        } catch (Exception e) {
            log.warn("统计数据查询失败: {}", e.getMessage());
        }
        return stats;
    }

    /**
     * 待办数量 — V3.2 §5.9.2
     */
    public Integer getTodoCount() {
        Integer userId = SecurityUtils.getCurrentUserId();
        String todoKey = Constants.REDIS_TODO_COUNT_PREFIX + userId;
        Object todoCount = redisTemplate.opsForValue().get(todoKey);
        return todoCount instanceof Number n ? n.intValue() : 0;
    }

    /**
     * 待办列表 — V3.2 §5.9.2
     * 业务模块完成后关联 biz_approval_instance 查询
     */
    public List<Map<String, Object>> getTodoList() {
        // TODO: 关联 biz_approval_instance 查询当前用户待审批的记录
        return new ArrayList<>();
    }

    /**
     * 更新待办数量缓存
     */
    public void updateTodoCount(Integer userId, int count) {
        String todoKey = Constants.REDIS_TODO_COUNT_PREFIX + userId;
        redisTemplate.opsForValue().set(todoKey, count,
                Constants.TODO_COUNT_CACHE_SECONDS, TimeUnit.SECONDS);
    }
}
