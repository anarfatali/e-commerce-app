package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.response.ProductDetailResponse;
import az.company.ecommerceapp.dto.response.ProductPageResponse;
import az.company.ecommerceapp.service.ProductService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ProductPageResponse getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) @DecimalMin(value = "0.00", message = "minPrice must be zero or greater") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin(value = "0.00", message = "maxPrice must be zero or greater") BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Boolean bestSeller,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be zero or greater") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size must be at least 1") @Max(value = 100, message = "size must be at most 100") int size) {
        return productService.getProducts(categoryId, categorySlug, minPrice, maxPrice, sort, isNew, bestSeller, page, size);
    }

    @GetMapping("/search")
    public ProductPageResponse searchProducts(
            @RequestParam("q") @NotBlank(message = "Search query is required") String query,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be zero or greater") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size must be at least 1") @Max(value = 100, message = "size must be at most 100") int size) {
        return productService.searchProducts(query, page, size);
    }

    @GetMapping("/{slug}")
    public ProductDetailResponse getProduct(@PathVariable @NotBlank(message = "Product slug is required") String slug) {
        return productService.getProductBySlug(slug);
    }
}
