package com.ccsw.tutorial.config;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ResponsePage<T> extends PageImpl<T> {

    public ResponsePage() {
        super(List.of());
    }

    public ResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public ResponsePage(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
