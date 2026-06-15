package az.company.ecommerceapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Column(name = "shipping_full_name")
    private String fullName;

    @Column(name = "shipping_phone", length = 20)
    private String phone;

    @Column(name = "shipping_street")
    private String street;

    @Column(name = "shipping_city")
    private String city;

    @Column(name = "shipping_country")
    private String country;

    @Column(name = "shipping_postal_code", length = 20)
    private String postalCode;
}