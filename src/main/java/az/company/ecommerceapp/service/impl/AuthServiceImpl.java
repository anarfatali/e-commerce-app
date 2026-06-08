package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.*;
import az.company.ecommerceapp.dto.response.AuthResponse;
import az.company.ecommerceapp.exception.EmailAlreadyExistsException;
import az.company.ecommerceapp.exception.InvalidCredentialsException;
import az.company.ecommerceapp.exception.InvalidTokenException;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.model.entity.User;
import az.company.ecommerceapp.model.enums.Role;
import az.company.ecommerceapp.repository.UserRepository;
import az.company.ecommerceapp.security.JwtService;
import az.company.ecommerceapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Long> refreshTokenStore = new ConcurrentHashMap<>();
    private final Map<String, Long> emailVerifyTokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> resetPasswordStore = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.email());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);

        // simulating email verification
        String verifyToken = UUID.randomUUID().toString();
        emailVerifyTokenStore.put(verifyToken, user.getId());
        log.info("[LOCAL] Email verify token for {}: {}", user.getEmail(), verifyToken);

        return issueTokenPair(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return issueTokenPair(user);
    }

    @Override
    public void logout(String accessToken) {
        Long userId = jwtService.extractUserId(accessToken);
        refreshTokenStore.values().removeIf(id -> id.equals(userId));
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.refreshToken();

        Long userId = jwtService.extractUserId(token);
        String type = jwtService.extractType(token);

        if (!"refresh".equals(type)) {
            throw new InvalidTokenException("Not a refresh token");
        }
        if (!refreshTokenStore.containsKey(token)) {
            throw new InvalidTokenException("Refresh token not found or already used");
        }

        refreshTokenStore.remove(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return issueTokenPair(user);
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {
        Long userId = emailVerifyTokenStore.remove(request.token());
        if (userId == null) {
            throw new InvalidTokenException("Invalid or expired verification token");
        }
        // If you add an emailVerified field to User, set it here:
        // userRepository.findById(userId).ifPresent(u -> { u.setEmailVerified(true); userRepository.save(u); });
        log.info("[LOCAL] Email verified for userId: {}", userId);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            resetPasswordStore.put(resetToken, user.getEmail());
            log.info("[LOCAL] Password reset token for {}: {}", user.getEmail(), resetToken);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = resetPasswordStore.remove(request.token());
        if (email == null) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private AuthResponse issueTokenPair(User user) {
        String role = user.getRole().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), role);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), role);
        refreshTokenStore.put(refreshToken, user.getId());
        return new AuthResponse(accessToken, refreshToken);
    }
}
