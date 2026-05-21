package com.ccsw.tutorial.common.pagination;

import org.springframework.data.domain.PageRequest;


public final class PaginationConstraints {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PaginationConstraints() {
    }

    public static PageRequest normalizedPage(int page, int size, int maxSize) {
        int p = Math.max(0, page);
        int s = size <= 0 ? DEFAULT_SIZE : Math.min(size, maxSize);
        return PageRequest.of(p, s);
    }

    public static PageRequest normalizedPage(int page, int size) {
        return normalizedPage(page, size, MAX_SIZE);
    }
}
