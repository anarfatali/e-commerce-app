package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.request.ProductCreateRequest;
import az.company.ecommerceapp.dto.request.ProductUpdateRequest;
import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import az.company.ecommerceapp.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @PostMapping
    public ResponseEntity<ProductAdminResponse> createProduct(
            @RequestBody @Valid ProductCreateRequest request) {
        adminProductService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
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