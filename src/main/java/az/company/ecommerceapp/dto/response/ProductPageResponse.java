package az.company.ecommerceapp.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductPageResponse(
        List<ProductSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static ProductPageResponse from(Page<ProductSummaryResponse> page) {
        return new ProductPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
