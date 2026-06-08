package az.company.ecommerceapp.dto.request;

import az.company.ecommerceapp.model.enums.Gender;

import java.time.LocalDate;

public record UpdateProfileRequest(

        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        Gender gender
) {
}