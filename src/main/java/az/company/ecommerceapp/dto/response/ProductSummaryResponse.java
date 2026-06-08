package az.company.ecommerceapp.dto.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        String name,
        String slug,
        String categoryName,
        String categorySlug,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        BigDecimal activePrice,
        BigDecimal discountPercentage,
        BigDecimal averageRating,
        int reviewCount,
        boolean isNew,
        boolean bestSeller,
        String mainImageUrl
) {
}
