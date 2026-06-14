package az.company.ecommerceapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        @Size(max = 255)
        String name,

        @Size(max = 255)
        String slug,

        @DecimalMin(value = "0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        BigDecimal discountPrice,

        @Min(0)
        Integer stockQuantity,

        Long categoryId,

        Boolean active
) {
}

