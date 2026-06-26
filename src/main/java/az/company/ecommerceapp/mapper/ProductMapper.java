package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.ProductDetailResponse;
import az.company.ecommerceapp.dto.response.ProductSummaryResponse;
import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ProductMapper {

    public ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                product.getOriginalPrice(),
                product.getDiscountPrice(),
                activePrice(product),
                discountPercentage(product),
                product.getAverageRating(),
                (long) product.getStockQuantity(),
                product.getReviewCount(),
                product.isNew(),
                product.isBestSeller(),
                product.getMainImageUrl()
        );
    }

    public ProductDetailResponse toDetailResponse(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                product.getOriginalPrice(),
                product.getDiscountPrice(),
                activePrice(product),
                discountPercentage(product),
                product.getAverageRating(),
                (long) product.getStockQuantity(),
                product.getReviewCount(),
                product.isNew(),
                product.isBestSeller(),
                product.getMainImageUrl(),
                imageUrls(product)
        );
    }

    private BigDecimal activePrice(Product product) {
        return product.getDiscountPrice() == null
                ? product.getOriginalPrice()
                : product.getDiscountPrice();
    }

    private BigDecimal discountPercentage(Product product) {
        BigDecimal original = product.getOriginalPrice();
        BigDecimal discount = product.getDiscountPrice();
        if (original == null
                || discount == null
                || original.compareTo(BigDecimal.ZERO) <= 0
                || discount.compareTo(original) >= 0) {
            return BigDecimal.ZERO;
        }
        return original.subtract(discount)
                .multiply(BigDecimal.valueOf(100))
                .divide(original, 2, RoundingMode.HALF_UP);
    }

    private List<String> imageUrls(Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().stream()
                    .map(ProductImage::getUrl)
                    .toList();
        }
        if (product.getMainImageUrl() != null && !product.getMainImageUrl().isBlank()) {
            return List.of(product.getMainImageUrl());
        }
        return List.of();
    }
}