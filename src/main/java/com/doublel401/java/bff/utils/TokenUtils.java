package com.doublel401.java.bff.utils;

import com.doublel401.java.bff.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class TokenUtils {
    @Value("${bff.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${bff.refresh.token.expiration}")
    private long refreshTokenExpiration;

    private final Key hmacKey;

    public TokenUtils(@Value("${bff.jwt.secret}") String jwtSecret) {
        hmacKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * Helper method for generating access jwt token
     * @param username username
     * @param issuer issuer
     * @return jwt token
     */
    public String generateAccessToken(String username, String issuer) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTokenExpiration)))
                .setIssuer(issuer)
                .signWith(hmacKey)
                .compact();
    }

    /**
     * Helper method for generating refresh token
     *
     * @param username username
     * @return refresh token
     */
    public RefreshToken generateRefreshToken(String username) {
        return RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID())
                .expiredTime(Instant.now().plusSeconds(refreshTokenExpiration))
                .build();
    }

    /**
     * Helper for get username from jwt token
     * @param token jwt token
     * @return username
     */
    public String getUsernameFromAccessToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token);

        return claims.getBody().getSubject();
    }
}
