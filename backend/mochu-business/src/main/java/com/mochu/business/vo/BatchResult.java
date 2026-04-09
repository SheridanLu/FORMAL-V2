package com.mochu.business.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BatchResult {

    private int successCount;
    private int failCount;
    private List<BatchError> errors = new ArrayList<>();

    public BatchResult(int successCount, int failCount, List<BatchError> errors) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BatchError {
        private int row;
        private String field;
        private String message;
    }
}
