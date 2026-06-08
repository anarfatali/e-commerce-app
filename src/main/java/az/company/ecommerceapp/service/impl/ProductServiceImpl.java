package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.response.ProductDetailResponse;
import az.company.ecommerceapp.dto.response.ProductPageResponse;
import az.company.ecommerceapp.dto.response.ProductSummaryResponse;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.ProductMapper;
import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.enums.ProductSortOption;
import az.company.ecommerceapp.repository.ProductRepository;
import az.company.ecommerceapp.repository.specification.ProductSpecifications;
import az.company.ecommerceapp.service.CategoryService;
import az.company.ecommerceapp.service.ProductService;
import az.company.ecommerceapp.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductPageResponse getProducts(
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort,
            Boolean isNew,
            Boolean bestSeller,
            int page,
            int size) {
        validatePriceRange(minPrice, maxPrice);

        Set<Long> categoryIds = categoryService.resolveCategoryFilterIds(categoryId, categorySlug);
        ProductSortOption sortOption = ProductSortOption.from(sort);
        Page<ProductSummaryResponse> products = productRepository
                .findAll(
                        ProductSpecifications.publicCatalog(
                                categoryIds,
                                minPrice,
                                maxPrice,
                                sortOption,
                                isNew,
                                bestSeller,
                                null),
                        PageRequest.of(page, size)
                )
                .map(productMapper::toSummaryResponse);

        return ProductPageResponse.from(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPageResponse searchProducts(String query, int page, int size) {
        Page<ProductSummaryResponse> products = productRepository
                .findAll(
                        ProductSpecifications.publicCatalog(
                                null,
                                null,
                                null,
                                ProductSortOption.NEWEST,
                                null,
                                null,
                                query),
                        PageRequest.of(page, size)
                )
                .map(productMapper::toSummaryResponse);

        return ProductPageResponse.from(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductBySlug(String slug) {
        String normalizedSlug = SlugUtils.from(slug);
        if (normalizedSlug == null || normalizedSlug.isBlank()) {
            throw new IllegalArgumentException("Product slug is required");
        }

        Product product = productRepository.findBySlugAndActiveTrue(normalizedSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));

        return productMapper.toDetailResponse(product);
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
        }
    }
}
