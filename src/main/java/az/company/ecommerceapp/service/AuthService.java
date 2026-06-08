package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.*;
import az.company.ecommerceapp.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String accessToken);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}