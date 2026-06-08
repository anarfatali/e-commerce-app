package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.ProductDetailResponse;
import az.company.ecommerceapp.dto.response.ProductSummaryResponse;
import az.company.ecommerceapp.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categorySlug", source = "category.slug")
    @Mapping(target = "activePrice", source = ".", qualifiedByName = "activePrice")
    @Mapping(target = "discountPercentage", source = ".", qualifiedByName = "discountPercentage")
    @Mapping(target = "isNew", source = "new")
    ProductSummaryResponse toSummaryResponse(Product product);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categorySlug", source = "category.slug")
    @Mapping(target = "activePrice", source = ".", qualifiedByName = "activePrice")
    @Mapping(target = "discountPercentage", source = ".", qualifiedByName = "discountPercentage")
    @Mapping(target = "isNew", source = "new")
    @Mapping(target = "imageUrls", source = ".", qualifiedByName = "detailImages")
    ProductDetailResponse toDetailResponse(Product product);

    @Named("activePrice")
    default BigDecimal activePrice(Product product) {
        return product.getDiscountPrice() == null ? product.getOriginalPrice() : product.getDiscountPrice();
    }

    @Named("discountPercentage")
    default BigDecimal discountPercentage(Product product) {
        BigDecimal originalPrice = product.getOriginalPrice();
        BigDecimal discountPrice = product.getDiscountPrice();

        if (originalPrice == null
                || discountPrice == null
                || originalPrice.compareTo(BigDecimal.ZERO) <= 0
                || discountPrice.compareTo(originalPrice) >= 0) {
            return BigDecimal.ZERO;
        }

        return originalPrice.subtract(discountPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(originalPrice, 2, RoundingMode.HALF_UP);
    }

    @Named("detailImages")
    default List<String> detailImages(Product product) {
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            return List.copyOf(product.getImageUrls());
        }

        if (product.getMainImageUrl() != null && !product.getMainImageUrl().isBlank()) {
            return List.of(product.getMainImageUrl());
        }

        return List.of();
    }
}
