package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.CategoryTreeResponse;
import az.company.ecommerceapp.model.entity.Category;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    @Mapping(target = "children", expression = "java(toChildren(category, childrenByParentId))")
    CategoryTreeResponse toTreeResponse(Category category, @Context Map<Long, List<Category>> childrenByParentId);

    default List<CategoryTreeResponse> toChildren(Category category, @Context Map<Long, List<Category>> childrenByParentId) {
        return childrenByParentId
                .getOrDefault(category.getId(), List.of())
                .stream()
                .map(child -> toTreeResponse(child, childrenByParentId))
                .toList();
    }
}
