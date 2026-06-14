package az.company.ecommerceapp.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String slug,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @DecimalMin(value = "0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal discountPrice,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        @NotNull(message = "Category is required")
        Long categoryId,

        boolean active
) {}