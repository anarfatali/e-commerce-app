package az.company.ecommerceapp.dto.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        String categoryName,
        String categorySlug,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        BigDecimal activePrice,
        BigDecimal discountPercentage,
        BigDecimal averageRating,
        Long stockQuantity,
        int reviewCount,
        boolean isNew,
        boolean bestSeller,
        String mainImageUrl
) {
}
