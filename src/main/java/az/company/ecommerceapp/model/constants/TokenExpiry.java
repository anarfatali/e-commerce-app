package az.company.ecommerceapp.model.constants;

import java.time.Duration;

public final class TokenExpiry {
    
    private TokenExpiry() {
    }

    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    public static final Duration EMAIL_VERIFY_TOKEN_TTL = Duration.ofHours(24);
}