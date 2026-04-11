package com.mochu.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochu.business.dto.SupplierRatingDTO;
import com.mochu.business.entity.BizSupplier;
import com.mochu.business.entity.BizSupplierRating;
import com.mochu.business.mapper.BizSupplierMapper;
import com.mochu.business.mapper.BizSupplierRatingMapper;
import com.mochu.common.constant.Constants;
import com.mochu.common.exception.BusinessException;
import com.mochu.common.result.PageResult;
import com.mochu.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierRatingService {

    private final BizSupplierRatingMapper ratingMapper;
    private final BizSupplierMapper supplierMapper;

    public PageResult<BizSupplierRating> listRatings(Integer supplierId, Integer page, Integer size) {
        int p = (page == null || page < 1) ? Constants.DEFAULT_PAGE : page;
        int s = (size == null || size < 1) ? Constants.DEFAULT_SIZE : size;
        Page<BizSupplierRating> pageParam = new Page<>(p, s);
        LambdaQueryWrapper<BizSupplierRating> wrapper = new LambdaQueryWrapper<>();
        if (supplierId != null) wrapper.eq(BizSupplierRating::getSupplierId, supplierId);
        wrapper.orderByDesc(BizSupplierRating::getCreatedAt);
        ratingMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(pageParam.getRecords(), pageParam.getTotal(), p, s);
    }

    public void createRating(SupplierRatingDTO dto) {
        if (supplierMapper.selectById(dto.getSupplierId()) == null) {
            throw new BusinessException("供应商不存在");
        }
        BizSupplierRating rating = new BizSupplierRating();
        rating.setSupplierId(dto.getSupplierId());
        rating.setPurchaseId(dto.getPurchaseId());
        rating.setProjectId(dto.getProjectId());
        rating.setQualityScore(dto.getQualityScore() != null ? dto.getQualityScore() : 5);
        rating.setDeliveryScore(dto.getDeliveryScore() != null ? dto.getDeliveryScore() : 5);
        rating.setServiceScore(dto.getServiceScore() != null ? dto.getServiceScore() : 5);
        rating.setPriceScore(dto.getPriceScore() != null ? dto.getPriceScore() : 5);
        // 综合评分 = 各项平均
        int total = rating.getQualityScore() + rating.getDeliveryScore()
                + rating.getServiceScore() + rating.getPriceScore();
        rating.setTotalScore(BigDecimal.valueOf(total).divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP));
        rating.setCommentText(dto.getCommentText());
        rating.setCreatorId(SecurityUtils.getCurrentUserId());
        ratingMapper.insert(rating);
    }

    public void deleteRating(Integer id) {
        ratingMapper.deleteById(id);
    }

    /**
     * 供应商综合评分汇总（用于采购选择参考）
     */
    public List<Map<String, Object>> supplierRatingSummary() {
        List<BizSupplier> suppliers = supplierMapper.selectList(
                new LambdaQueryWrapper<BizSupplier>().eq(BizSupplier::getStatus, "active"));
        return suppliers.stream().map(s -> {
            List<BizSupplierRating> ratings = ratingMapper.selectList(
                    new LambdaQueryWrapper<BizSupplierRating>()
                            .eq(BizSupplierRating::getSupplierId, s.getId()));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("supplierId", s.getId());
            row.put("supplierName", s.getSupplierName());
            row.put("ratingCount", ratings.size());
            if (ratings.isEmpty()) {
                row.put("avgQuality", null);
                row.put("avgDelivery", null);
                row.put("avgService", null);
                row.put("avgPrice", null);
                row.put("avgTotal", null);
            } else {
                row.put("avgQuality", avg(ratings.stream().map(r -> r.getQualityScore().doubleValue()).collect(Collectors.toList())));
                row.put("avgDelivery", avg(ratings.stream().map(r -> r.getDeliveryScore().doubleValue()).collect(Collectors.toList())));
                row.put("avgService", avg(ratings.stream().map(r -> r.getServiceScore().doubleValue()).collect(Collectors.toList())));
                row.put("avgPrice", avg(ratings.stream().map(r -> r.getPriceScore().doubleValue()).collect(Collectors.toList())));
                row.put("avgTotal", avg(ratings.stream().map(r -> r.getTotalScore().doubleValue()).collect(Collectors.toList())));
            }
            return row;
        }).collect(Collectors.toList());
    }

    private double avg(List<Double> vals) {
        return vals.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
