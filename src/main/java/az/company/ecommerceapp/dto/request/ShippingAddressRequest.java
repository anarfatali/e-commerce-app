package az.company.ecommerceapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingAddressRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20)
        String phone,

        @NotBlank(message = "Street address is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Country is required")
        String country,

        @Size(max = 20)
        String postalCode
) {}