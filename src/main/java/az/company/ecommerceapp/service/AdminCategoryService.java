package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.CategoryCreateRequest;
import az.company.ecommerceapp.dto.request.CategoryUpdateRequest;
import az.company.ecommerceapp.dto.response.CategoryAdminResponse;

import java.util.List;

public interface AdminCategoryService {

    void createCategory(CategoryCreateRequest request);

    CategoryAdminResponse updateCategory(Long id, CategoryUpdateRequest request);

    void deleteCategory(Long id);

    CategoryAdminResponse getCategoryById(Long id);

    List<CategoryAdminResponse> getAllCategories();
}