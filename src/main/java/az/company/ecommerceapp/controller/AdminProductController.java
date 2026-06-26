package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.request.ProductCreateRequest;
import az.company.ecommerceapp.dto.request.ProductUpdateRequest;
import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import az.company.ecommerceapp.dto.response.ProductPageResponse;
import az.company.ecommerceapp.service.AdminProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @PostMapping
    public ResponseEntity<ProductAdminResponse> createProduct(
            @RequestBody @Valid ProductCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminProductService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseEntity.ok(adminProductService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.getProductById(id));
    }

    @GetMapping
    public ProductPageResponse getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) @DecimalMin(value = "0.00", message = "minPrice must be zero or greater") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin(value = "0.00", message = "maxPrice must be zero or greater") BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Boolean bestSeller,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be zero or greater") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size must be at least 1") @Max(value = 100, message = "size must be at most 100") int size) {
        return adminProductService.getAllProducts(categoryId, categorySlug, minPrice, maxPrice, sort, isNew, bestSeller, page, size);
    }

    @PostMapping(
            value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> addImage(
            @PathVariable Long productId,
            @RequestPart("file") MultipartFile file) {

        adminProductService.addImage(productId, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        adminProductService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}