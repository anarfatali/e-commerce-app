package az.company.ecommerceapp.repository;

import az.company.ecommerceapp.model.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpiresAtBefore(LocalDateTime now);
}
