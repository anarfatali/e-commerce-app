package az.company.ecommerceapp.dto.request;

import az.company.ecommerceapp.model.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        String phoneNumber,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        Gender gender
) {
}