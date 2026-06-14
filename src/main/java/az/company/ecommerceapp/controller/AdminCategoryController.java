package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.request.CategoryCreateRequest;
import az.company.ecommerceapp.dto.request.CategoryUpdateRequest;
import az.company.ecommerceapp.dto.response.CategoryAdminResponse;
import az.company.ecommerceapp.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<CategoryAdminResponse> createCategory(
            @RequestBody @Valid CategoryCreateRequest request) {
        adminCategoryService.createCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryAdminResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateRequest request) {
        return ResponseEntity.ok(adminCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryAdminResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(adminCategoryService.getCategoryById(id));
    }
}