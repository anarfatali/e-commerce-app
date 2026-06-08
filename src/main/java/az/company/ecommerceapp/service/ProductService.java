package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.response.ProductDetailResponse;
import az.company.ecommerceapp.dto.response.ProductPageResponse;

import java.math.BigDecimal;

public interface ProductService {

    ProductPageResponse getProducts(
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort,
            Boolean isNew,
            Boolean bestSeller,
            int page,
            int size);

    ProductPageResponse searchProducts(String query, int page, int size);

    ProductDetailResponse getProductBySlug(String slug);
}
