package org.springframework.data.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.LongSupplier;

public abstract class PageableExecutionUtils {

    private PageableExecutionUtils() {
    }

    public static <T> Page<T> getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(content, pageable, content.size());
        }
        long total = pageable.getOffset();
        if (content.size() > 0) {
            total = total + content.size();
        }
        if (content.size() == pageable.getPageSize()) {
            total = total + 1;
        }
        return new PageImpl<>(content, pageable, total);
    }
}
