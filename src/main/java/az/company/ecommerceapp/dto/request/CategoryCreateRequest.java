package az.company.ecommerceapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String slug,

        @Size(max = 1024)
        String imageUrl,

        Long parentId
) {}
