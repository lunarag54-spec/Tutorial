package com.ccsw.tutorial.common.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableRequest {

    private int pageNumber;
    private int pageSize;
    private Sort sort;

    public PageableRequest() {
    }

    public PageableRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Pageable getPageable() {
        return PaginationConstraints.normalizedPage(this.pageNumber, this.pageSize, PaginationConstraints.MAX_SIZE)
                .withSort(this.sort != null ? this.sort : Sort.unsorted());
    }
}
