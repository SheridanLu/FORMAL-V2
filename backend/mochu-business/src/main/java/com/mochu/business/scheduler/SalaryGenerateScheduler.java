package com.mochu.business.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizSalary;
import com.mochu.business.entity.BizSalaryConfig;
import com.mochu.business.entity.BizTaxRate;
import com.mochu.business.mapper.BizSalaryMapper;
import com.mochu.business.mapper.BizSalaryConfigMapper;
import com.mochu.business.mapper.BizTaxRateMapper;
import com.mochu.system.entity.SysUser;
import com.mochu.system.mapper.SysUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

/**
 * 工资表自动生成
 * Cron: 0 0 1 25 * ? — 每月25日 01:00
 * 功能: 自动计算工资、社保、个税（累计预扣法），生成工资条
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SalaryGenerateScheduler {

    private final SysUserMapper userMapper;
    private final BizSalaryMapper salaryMapper;
    private final BizSalaryConfigMapper salaryConfigMapper;
    private final BizTaxRateMapper taxRateMapper;
    private final StringRedisTemplate redisTemplate;

    /** 个税起征点 */
    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");

    @XxlJob("salaryGenerateJob")
    public void generateMonthlySalary() {
        String lockKey = "scheduler:lock:salary_generate";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(30));
        if (Boolean.FALSE.equals(locked)) return;

        try {
            YearMonth currentMonth = YearMonth.now();
            log.info("=== {}月工资表生成开始 ===", currentMonth);

            // 查询所有在职员工
            List<SysUser> employees = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getStatus, 1));

            // 加载税率表
            List<BizTaxRate> taxRates = taxRateMapper.selectList(
                    new LambdaQueryWrapper<BizTaxRate>()
                            .orderByAsc(BizTaxRate::getLevel));

            int count = 0;
            for (SysUser emp : employees) {
                try {
                    generateForEmployee(emp, currentMonth, taxRates);
                    count++;
                } catch (Exception e) {
                    log.error("员工{}工资生成失败: {}", emp.getUsername(), e.getMessage());
                }
            }
            log.info("=== 工资表生成完成: 成功{}条 ===", count);
        } catch (Exception e) {
            log.error("工资表生成异常", e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * #N1 fix: 每位员工独立事务，单人失败不影响整批
     * #N2 fix: 先检查当月是否已存在工资记录，防止重复生成
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void generateForEmployee(SysUser emp, YearMonth month,
                                      List<BizTaxRate> taxRates) {
        // #N2 fix: 重复检查 — 当月已存在则跳过
        Long existCount = salaryMapper.selectCount(
                new LambdaQueryWrapper<BizSalary>()
                        .eq(BizSalary::getUserId, emp.getId())
                        .eq(BizSalary::getSalaryMonth, month.toString()));
        if (existCount != null && existCount > 0) {
            log.info("员工{}当月工资已存在，跳过", emp.getUsername());
            return;
        }

        // 查询有效薪资配置
        BizSalaryConfig config = salaryConfigMapper.selectOne(
                new LambdaQueryWrapper<BizSalaryConfig>()
                        .eq(BizSalaryConfig::getUserId, emp.getId())
                        .eq(BizSalaryConfig::getStatus, "active")
                        .le(BizSalaryConfig::getEffectiveDate, month.atEndOfMonth())
                        .orderByDesc(BizSalaryConfig::getEffectiveDate)
                        .last("LIMIT 1"));

        if (config == null) {
            log.warn("员工{}无有效薪资配置，跳过", emp.getUsername());
            return;
        }

        // 总工资 = 基本工资 + 岗位工资 + 绩效 + 补贴
        BigDecimal grossSalary = BigDecimal.ZERO
                .add(nvl(config.getBaseSalary()))
                .add(nvl(config.getPositionSalary()))
                .add(nvl(config.getPerformance()))
                .add(nvl(config.getAllowance()));

        // 社保个人扣款（简化：按固定比例）
        BigDecimal socialInsurance = grossSalary.multiply(new BigDecimal("0.105"))
                .setScale(2, RoundingMode.HALF_UP);

        // 累计预扣法计算个税
        BigDecimal tax = calculateTax(emp.getId(), month, grossSalary,
                socialInsurance, taxRates);

        // 实发工资
        BigDecimal netSalary = grossSalary.subtract(socialInsurance).subtract(tax);

        // 生成工资记录
        BizSalary salary = new BizSalary();
        salary.setUserId(emp.getId());
        salary.setSalaryMonth(month.toString());
        salary.setBaseSalary(config.getBaseSalary());
        salary.setPositionSalary(config.getPositionSalary());
        salary.setPerformance(config.getPerformance());
        salary.setAllowance(config.getAllowance());
        salary.setGrossSalary(grossSalary);
        salary.setSocialInsurance(socialInsurance);
        salary.setTax(tax);
        salary.setNetSalary(netSalary);
        salary.setStatus("draft");
        salaryMapper.insert(salary);
    }

    /**
     * 累计预扣法计算个税
     * 本月应扣 = ROUND(累计应纳税所得额 × 税率 - 速算扣除数 - 已累计扣税, 2)
     */
    private BigDecimal calculateTax(Integer userId, YearMonth currentMonth,
                                     BigDecimal gross, BigDecimal social,
                                     List<BizTaxRate> taxRates) {
        // 查询本年度已发放工资（累计）
        String yearStart = currentMonth.getYear() + "-01";
        List<BizSalary> prevSalaries = salaryMapper.selectList(
                new LambdaQueryWrapper<BizSalary>()
                        .eq(BizSalary::getUserId, userId)
                        .ge(BizSalary::getSalaryMonth, yearStart)
                        .lt(BizSalary::getSalaryMonth, currentMonth.toString())
                        .ne(BizSalary::getStatus, "cancelled"));

        // 已累计扣税
        BigDecimal cumulativeTaxPaid = prevSalaries.stream()
                .map(s -> nvl(s.getTax()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 累计应纳税所得额 = 累计工资总额 - 累计社保 - 累计起征点
        BigDecimal cumulativeGross = prevSalaries.stream()
                .map(s -> nvl(s.getGrossSalary()))  // grossSalary field added to BizSalary
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(gross);
        BigDecimal cumulativeSocial = prevSalaries.stream()
                .map(s -> nvl(s.getSocialInsurance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(social);
        int months = prevSalaries.size() + 1;
        BigDecimal cumulativeThreshold = TAX_THRESHOLD.multiply(
                new BigDecimal(months));

        BigDecimal taxableIncome = cumulativeGross.subtract(cumulativeSocial)
                .subtract(cumulativeThreshold);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 查找适用税率
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal deduction = BigDecimal.ZERO;
        for (BizTaxRate tr : taxRates) {
            if (taxableIncome.compareTo(tr.getMinIncome()) > 0) {
                rate = tr.getRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                deduction = tr.getDeduction();
            }
        }

        // 本月应扣 = 累计应纳税额 - 已累计扣税
        BigDecimal cumulativeTax = taxableIncome.multiply(rate)
                .subtract(deduction);
        BigDecimal currentTax = cumulativeTax.subtract(cumulativeTaxPaid)
                .setScale(2, RoundingMode.HALF_UP);

        return currentTax.max(BigDecimal.ZERO);
    }

    private BigDecimal nvl(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}
