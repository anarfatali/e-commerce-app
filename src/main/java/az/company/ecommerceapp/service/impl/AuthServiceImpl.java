package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.*;
import az.company.ecommerceapp.dto.response.AuthResponse;
import az.company.ecommerceapp.exception.EmailAlreadyExistsException;
import az.company.ecommerceapp.exception.InvalidCredentialsException;
import az.company.ecommerceapp.exception.InvalidTokenException;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.model.constants.TokenExpiry;
import az.company.ecommerceapp.model.entity.EmailVerificationToken;
import az.company.ecommerceapp.model.entity.PasswordResetToken;
import az.company.ecommerceapp.model.entity.RefreshToken;
import az.company.ecommerceapp.model.entity.User;
import az.company.ecommerceapp.model.enums.Role;
import az.company.ecommerceapp.repository.EmailVerificationTokenRepository;
import az.company.ecommerceapp.repository.PasswordResetTokenRepository;
import az.company.ecommerceapp.repository.RefreshTokenRepository;
import az.company.ecommerceapp.repository.UserRepository;
import az.company.ecommerceapp.security.JwtService;
import az.company.ecommerceapp.service.AuthService;
import az.company.ecommerceapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
                .emailVerified(false)
                .build();

        userRepository.save(user);
        sendEmailVerification(user);

        return issueTokenPair(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!user.isEmailVerified()) {
            throw new InvalidCredentialsException("Email address is not verified");
        }

        return issueTokenPair(user);
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtService.extractUserId(accessToken);
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String rawToken = request.refreshToken();

        if (!"refresh".equals(jwtService.extractType(rawToken))) {
            throw new InvalidTokenException("Not a refresh token");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found or already used"));

        if (stored.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored);
            throw new InvalidTokenException("Refresh token has expired");
        }

        refreshTokenRepository.delete(stored);

        return issueTokenPair(stored.getUser());
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken record = emailVerificationTokenRepository
                .findByToken(request.code())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification code"));

        if (record.isUsed()) {
            throw new InvalidTokenException("Verification code already used");
        }

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            emailVerificationTokenRepository.delete(record);
            throw new InvalidTokenException("Verification code has expired. Request a new one.");
        }

        User user = record.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        record.setUsed(true);
        emailVerificationTokenRepository.save(record);

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            passwordResetTokenRepository.deleteAllByUserId(user.getId());

            if (!user.isEmailVerified()) {
                throw new InvalidCredentialsException("Email address is not verified. Please verify your email.");
            }

            String code = generateSixDigitCode();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(code)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();

            passwordResetTokenRepository.save(resetToken);

            emailService.sendPasswordResetCodeEmail(user.getEmail(), code);
            log.info("Password reset code issued for user: {}", user.getEmail());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken record = passwordResetTokenRepository
                .findByToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        if (record.isUsed()) {
            throw new InvalidTokenException("Reset token already used");
        }

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(record);
            throw new InvalidTokenException("Reset token has expired. Request a new one.");
        }

        User user = record.getUser();

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must differ from the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        record.setUsed(true);
        passwordResetTokenRepository.save(record);
        refreshTokenRepository.deleteAllByUserId(user.getId());

        log.info("Password reset completed for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must differ from the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        refreshTokenRepository.deleteAllByUserId(userId);

        log.info("Password changed for userId: {}", userId);
    }

    private AuthResponse issueTokenPair(User user) {
        String role = user.getRole().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), role);
        String rawRefresh = jwtService.generateRefreshToken(user.getId(), role);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(rawRefresh)
                .user(user)
                .expiresAt(LocalDateTime.now().plus(TokenExpiry.REFRESH_TOKEN_TTL))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefresh);
    }

    private void sendEmailVerification(User user) {
        emailVerificationTokenRepository.deleteAllByUserId(user.getId());

        String code = generateSixDigitCode();

        EmailVerificationToken verifyToken = EmailVerificationToken.builder()
                .token(code)
                .user(user)
                .expiresAt(LocalDateTime.now().plus(TokenExpiry.EMAIL_VERIFY_TOKEN_TTL))
                .build();

        emailVerificationTokenRepository.save(verifyToken);

        emailService.sendVerificationCodeEmail(user.getEmail(), code);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    private String generateSixDigitCode() {
        int code = new SecureRandom().nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}