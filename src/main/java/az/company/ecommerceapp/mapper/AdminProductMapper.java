package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import az.company.ecommerceapp.dto.response.ProductImageResponse;
import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminProductMapper {

    public ProductAdminResponse toAdminResponse(Product product) {
        List<ProductImageResponse> images = product.getImages().stream()
                .map(this::toImageResponse)
                .toList();

        return new ProductAdminResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getDiscountPrice(),
                product.getStockQuantity(),
                product.getAverageRating(),
                product.getReviewCount(),
                product.isActive(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                images,
                product.getCreatedDate(),
                product.getUpdatedDate()
        );
    }

    private ProductImageResponse toImageResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getUrl(),
                image.getPublicId()
        );
    }
}