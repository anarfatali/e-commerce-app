package az.company.ecommerceapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "local-dev-secret-key-32-chars-ok!";
    private static final long ACCESS_EXPIRY_MS  = 1000 * 60 * 60;        // 1 hour
    private static final long REFRESH_EXPIRY_MS = 1000 * 60 * 60 * 24 * 7; // 7 days

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateAccessToken(Long userId, String role) {
        return buildToken(userId, role, "access", ACCESS_EXPIRY_MS);
    }

    public String generateRefreshToken(Long userId, String role) {
        return buildToken(userId, role, "refresh", REFRESH_EXPIRY_MS);
    }

    public Long extractUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String extractType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    private String buildToken(Long userId, String role, String type, long expiryMs) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}