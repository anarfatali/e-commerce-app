package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.ProductCreateRequest;
import az.company.ecommerceapp.dto.request.ProductUpdateRequest;
import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import az.company.ecommerceapp.dto.response.ProductPageResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface AdminProductService {

    ProductAdminResponse createProduct(ProductCreateRequest request);

    ProductAdminResponse updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);

    ProductAdminResponse getProductById(Long id);

    void addImage(Long productId, MultipartFile file);

    void deleteImage(Long productId, Long imageId);

    ProductPageResponse getAllProducts(Long categoryId, String categorySlug, BigDecimal minPrice, BigDecimal maxPrice, String sort, Boolean isNew, Boolean bestSeller, int page, int size);
}