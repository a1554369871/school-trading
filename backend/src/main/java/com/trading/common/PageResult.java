package com.trading.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;

    public static <T> PageResult<T> of(List<T> list, long total, int pageNum, int pageSize) {
        PageResult<T> r = new PageResult<>();
        r.setList(list);
        r.setTotal(total);
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        r.setPages((int) Math.ceil((double) total / pageSize));
        return r;
    }
}
