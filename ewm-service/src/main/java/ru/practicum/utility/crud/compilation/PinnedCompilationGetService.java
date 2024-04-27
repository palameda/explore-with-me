package ru.practicum.utility.crud.compilation;

import java.util.Collection;

public interface PinnedCompilationGetService<D> {
    Collection<D> getCompilationsByPinned(Boolean pinned, Integer from, Integer size);
}
