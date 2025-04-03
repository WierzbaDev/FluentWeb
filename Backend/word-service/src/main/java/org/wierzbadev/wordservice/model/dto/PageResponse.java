package org.wierzbadev.wordservice.model.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        long totalPages,
        int size,
        int number
) {}
