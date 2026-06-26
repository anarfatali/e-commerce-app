package az.company.ecommerceapp.dto.response;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String name,
        String slug,
        List<CategoryTreeResponse> children
) {
}
