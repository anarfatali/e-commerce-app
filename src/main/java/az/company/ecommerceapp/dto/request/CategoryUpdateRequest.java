package az.company.ecommerceapp.dto.request;

import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(

        @Size(max = 255)
        String name,

        @Size(max = 255)
        String slug,

        String description,

        @Size(max = 1024)
        String imageUrl,

        Long parentId,

        Boolean active
) {
}

