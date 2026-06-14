package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.CategoryCreateRequest;
import az.company.ecommerceapp.dto.request.CategoryUpdateRequest;
import az.company.ecommerceapp.dto.response.CategoryAdminResponse;
import az.company.ecommerceapp.exception.DuplicateSlugException;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.AdminCategoryMapper;
import az.company.ecommerceapp.model.entity.Category;
import az.company.ecommerceapp.repository.CategoryRepository;
import az.company.ecommerceapp.repository.ProductRepository;
import az.company.ecommerceapp.service.AdminCategoryService;
import az.company.ecommerceapp.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AdminCategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryAdminResponse createCategory(CategoryCreateRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateSlugException("Category slug already exists: " + slug);
        }

        Category parent = resolveParent(request.parentId(), null);

        Category category = new Category();
        category.setName(request.name());
        category.setSlug(slug);
        category.setImageUrl(request.imageUrl());
        category.setParent(parent);
        category.setActive(true);

        return categoryMapper.toAdminResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryAdminResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        if (request.name() != null) {
            category.setName(request.name());
        }

        if (request.slug() != null) {
            String newSlug = SlugUtils.from(request.slug());
            if (!newSlug.equals(category.getSlug()) && categoryRepository.existsBySlug(newSlug)) {
                throw new DuplicateSlugException("Category slug already exists: " + newSlug);
            }
            category.setSlug(newSlug);
        }

        if (request.imageUrl() != null) {
            category.setImageUrl(request.imageUrl());
        }

        if (request.active() != null) {
            category.setActive(request.active());
        }

        // parentId == null  → don't change
        // parentId == 0     → remove parent (make top-level)
        // parentId > 0      → assign new parent
        if (request.parentId() != null) {
            category.setParent(resolveParent(request.parentId(), id));
        }

        return categoryMapper.toAdminResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        if (productRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new IllegalStateException(
                    "Cannot delete a category that has active products. Deactivate or reassign them first.");
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryAdminResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toAdminResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private String resolveSlug(String provided, String name) {
        return (provided != null && !provided.isBlank())
                ? SlugUtils.from(provided)
                : SlugUtils.from(name);
    }

    private Category resolveParent(Long parentId, Long selfId) {
        if (parentId == null || parentId == 0L) {
            return null;
        }
        if (parentId.equals(selfId)) {
            throw new IllegalArgumentException("A category cannot be its own parent");
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + parentId));
    }
}