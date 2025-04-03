package org.wierzbadev.userservice.dto.projection;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        long totalPages,
        int size,
        int number
) {}
