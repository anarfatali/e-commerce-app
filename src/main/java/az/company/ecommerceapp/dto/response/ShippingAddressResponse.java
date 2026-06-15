package az.company.ecommerceapp.dto.response;

public record ShippingAddressResponse(
        String fullName,
        String phone,
        String street,
        String city,
        String country,
        String postalCode
) {
}