package az.company.ecommerceapp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductAdminResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal discountPrice,
        int stockQuantity,
        BigDecimal averageRating,
        int reviewCount,
        boolean active,
        Long categoryId,
        String categoryName,
        List<ProductImageResponse> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
