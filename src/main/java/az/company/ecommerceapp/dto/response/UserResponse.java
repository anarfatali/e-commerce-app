package az.company.ecommerceapp.dto.response;

import az.company.ecommerceapp.model.enums.Gender;
import az.company.ecommerceapp.model.enums.Role;
import az.company.ecommerceapp.model.enums.UserStatus;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        Gender gender,
        String avatarUrl,
        Role role,
        UserStatus status,
        boolean emailVerified
) {
}