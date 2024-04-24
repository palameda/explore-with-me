package ru.practicum.api.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Page extends PageRequest {
    private final int from;

    public Page(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
