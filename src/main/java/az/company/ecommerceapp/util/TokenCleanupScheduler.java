package az.company.ecommerceapp.util;

import az.company.ecommerceapp.repository.EmailVerificationTokenRepository;
import az.company.ecommerceapp.repository.PasswordResetTokenRepository;
import az.company.ecommerceapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteAllByExpiresAtBefore(now);
        emailVerificationTokenRepository.deleteAllByExpiresAtBefore(now);
        passwordResetTokenRepository.deleteAllByExpiresAtBefore(now);
        log.info("Expired tokens purged at {}", now);
    }
}