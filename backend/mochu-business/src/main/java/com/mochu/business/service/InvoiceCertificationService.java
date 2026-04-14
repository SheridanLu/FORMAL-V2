package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mochu.business.entity.BizInvoice;
import com.mochu.business.mapper.BizInvoiceMapper;
import com.mochu.system.entity.SysTodo;
import com.mochu.system.mapper.SysTodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceCertificationService {

    private final BizInvoiceMapper invoiceMapper;
    private final SysTodoMapper todoMapper;

    /** 认证期限(天) */
    private static final int CERT_PERIOD_DAYS = 360;
    /** 预警提前天数 */
    private static final int WARNING_DAYS = 30;

    /**
     * 扫描即将到期的专票并预警
     */
    public void checkCertificationExpiry() {
        LocalDate today = LocalDate.now();
        // 截止日期 = 开票日 + 360天
        // 预警条件：截止日期 - 今天 <= 30天 且 > 0天
        LocalDate latestInvoiceDate = today.minusDays(CERT_PERIOD_DAYS - WARNING_DAYS);
        LocalDate earliestInvoiceDate = today.minusDays(CERT_PERIOD_DAYS);

        List<BizInvoice> expiringInvoices = invoiceMapper.selectList(
                new LambdaQueryWrapper<BizInvoice>()
                        .eq(BizInvoice::getInvoiceType, "special") // 专票
                        .eq(BizInvoice::getIsCertified, 0)         // 未认证
                        .between(BizInvoice::getInvoiceDate,
                                earliestInvoiceDate, latestInvoiceDate)
                        .eq(BizInvoice::getDeleted, 0));

        for (BizInvoice invoice : expiringInvoices) {
            long remainDays = ChronoUnit.DAYS.between(today,
                    invoice.getInvoiceDate().plusDays(CERT_PERIOD_DAYS));

            SysTodo todo = new SysTodo();
            todo.setUserId(invoice.getCreatorId()); // 通知录入人（财务）
            todo.setTitle(String.format("【发票预警】发票%s认证还剩%d天到期",
                    invoice.getInvoiceNo(), remainDays));
            todo.setContent(String.format("发票号码: %s, 开票日期: %s, 认证截止: %s",
                    invoice.getInvoiceNo(), invoice.getInvoiceDate(),
                    invoice.getInvoiceDate().plusDays(CERT_PERIOD_DAYS)));
            todo.setBizType("invoice_cert_warning");
            todo.setBizId(invoice.getId());
            todo.setStatus(0);
            todoMapper.insert(todo);

            log.info("发票认证预警: invoiceNo={}, 剩余{}天",
                    invoice.getInvoiceNo(), remainDays);
        }
    }
}
