package vn.hoidanit.springrestwithai.dto;

import java.util.List;

public record PaginationDTO<T>(
        Meta meta,
        List<T> result
) {
    public record Meta(
            int page,
            int pageSize,
            int pages,
            long total
    ) {}
}
