package az.company.ecommerceapp.dto.response;

import java.time.LocalDateTime;

public record CategoryAdminResponse(
        Long id,
        String name,
        String slug,
        String imageUrl,
        boolean active,
        Long parentId,
        String parentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

