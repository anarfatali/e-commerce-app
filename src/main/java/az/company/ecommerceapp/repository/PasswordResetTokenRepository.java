package az.company.ecommerceapp.repository;

import az.company.ecommerceapp.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpiresAtBefore(LocalDateTime now);
}