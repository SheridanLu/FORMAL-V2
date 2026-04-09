package com.mochu.common.result;

import lombok.Data;

import java.util.List;

/**
 * 分页响应 — 对照 V3.2 §3.3
 * {records, total, page, size, pages}
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;
    private int pages;

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }
}
