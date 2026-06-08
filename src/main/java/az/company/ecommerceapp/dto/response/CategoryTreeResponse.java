package az.company.ecommerceapp.dto.response;

import java.util.List;

public record CategoryTreeResponse(
        String name,
        String slug,
        List<CategoryTreeResponse> children
) {
}
