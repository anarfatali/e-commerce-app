package az.company.ecommerceapp.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String slug,
        String description,
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
        String mainImageUrl,
        List<String> imageUrls
) {
}
