package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.response.CategoryTreeResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    List<CategoryTreeResponse> getCategoryTree();

    Set<Long> resolveCategoryFilterIds(Long categoryId, String categorySlug);
}
