package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.response.CategoryTreeResponse;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.CategoryMapper;
import az.company.ecommerceapp.model.entity.Category;
import az.company.ecommerceapp.repository.CategoryRepository;
import az.company.ecommerceapp.service.CategoryService;
import az.company.ecommerceapp.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc();
        Map<Long, List<Category>> childrenByParentId = buildChildrenMap(categories);

        return findRoots(categories).stream()
                .map(root -> categoryMapper.toTreeResponse(root, childrenByParentId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> resolveCategoryFilterIds(Long categoryId, String categorySlug) {
        if (categoryId == null && (categorySlug == null || categorySlug.isBlank())) {
            return null;
        }

        Category selected = findActiveCategory(categoryId, categorySlug);
        List<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc();
        Map<Long, List<Category>> childrenByParentId = buildChildrenMap(categories);

        return collectDescendantIds(selected, childrenByParentId);
    }

    private Category findActiveCategory(Long categoryId, String categorySlug) {
        if (categorySlug != null && !categorySlug.isBlank()) {
            String normalizedSlug = SlugUtils.from(categorySlug);
            Category category = categoryRepository.findBySlugAndActiveTrue(normalizedSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categorySlug));

            if (categoryId != null && !categoryId.equals(category.getId())) {
                throw new IllegalArgumentException("categoryId and categorySlug refer to different categories");
            }

            return category;
        }

        return categoryRepository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private List<Category> findRoots(List<Category> categories) {
        return categories.stream()
                .filter(category -> category.getParent() == null)
                .toList();
    }

    private Map<Long, List<Category>> buildChildrenMap(List<Category> categories) {
        Map<Long, List<Category>> childrenByParentId = new HashMap<>();

        for (Category category : categories) {
            Category parent = category.getParent();
            if (parent != null) {
                childrenByParentId.computeIfAbsent(parent.getId(), ignored -> new ArrayList<>()).add(category);
            }
        }

        return childrenByParentId;
    }

    private Set<Long> collectDescendantIds(Category root, Map<Long, List<Category>> childrenByParentId) {
        Set<Long> categoryIds = new HashSet<>();
        ArrayDeque<Category> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Category category = queue.removeFirst();
            categoryIds.add(category.getId());
            queue.addAll(childrenByParentId.getOrDefault(category.getId(), List.of()));
        }

        return categoryIds;
    }
}
