package com.mochu.business.service;

import com.mochu.business.entity.BizNoSeed;
import com.mochu.business.mapper.BizNoSeedMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 编号生成服务
 * 生成格式: 前缀 + 日期部分 + 序号（如 PJ260407001）
 */
@Service
@RequiredArgsConstructor
public class NoGeneratorService {

    private final BizNoSeedMapper noSeedMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    /**
     * 生成编号
     * @param prefix 前缀（如 PJ/CT/PO 等）
     * @param seqWidth 序号位数（如 3 → 001）
     */
    @Transactional
    public String generate(String prefix, int seqWidth) {
        String datePart = LocalDate.now().format(DATE_FMT);
        BizNoSeed seed = noSeedMapper.selectForUpdate(prefix, datePart);

        int nextSeq;
        if (seed == null) {
            nextSeq = 1;
            seed = new BizNoSeed();
            seed.setPrefix(prefix);
            seed.setDatePart(datePart);
            seed.setCurrentSeq(nextSeq);
            noSeedMapper.insert(seed);
        } else {
            nextSeq = seed.getCurrentSeq() + 1;
            seed.setCurrentSeq(nextSeq);
            noSeedMapper.updateSeq(seed);
        }

        String seqStr = String.format("%0" + seqWidth + "d", nextSeq);
        return prefix + datePart + seqStr;
    }

    /**
     * 生成编号（默认3位序号）
     */
    @Transactional
    public String generate(String prefix) {
        return generate(prefix, 3);
    }
}
