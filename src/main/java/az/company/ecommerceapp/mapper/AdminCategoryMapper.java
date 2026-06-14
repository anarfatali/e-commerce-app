package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.CategoryAdminResponse;
import az.company.ecommerceapp.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class AdminCategoryMapper {

    public CategoryAdminResponse toAdminResponse(Category category) {
        Category parent = category.getParent();
        return new CategoryAdminResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getImageUrl(),
                category.isActive(),
                parent != null ? parent.getId() : null,
                parent != null ? parent.getName() : null,
                category.getCreatedDate(),
                category.getUpdatedDate()
        );
    }
}
