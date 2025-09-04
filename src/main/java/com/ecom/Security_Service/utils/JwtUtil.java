package com.ecom.Security_Service.utils;

import com.ecom.Security_Service.entity.RefreshToken;
import com.ecom.Security_Service.entity.Role;
import com.ecom.Security_Service.entity.User;
import com.ecom.Security_Service.exception.ResourceNotFoundException;
import com.ecom.Security_Service.exception.TokenRefreshException;
import com.ecom.Security_Service.repo.RefreshTokenRepository;
import com.ecom.Security_Service.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationInMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationDateInMs;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .toList());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public RefreshToken generateRefreshToken(long userId, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        RefreshToken rfToken = new RefreshToken();
        rfToken.setRefreshToken(UUID.randomUUID().toString());
        rfToken.setDeviceId(deviceId);
        rfToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationDateInMs));
        rfToken.setUser(user);

        return refreshTokenRepository.save(rfToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    @Transactional
    public RefreshToken verifyExpirationAndRotate(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getRefreshToken(), "Refresh token expired. Please login again.");
        }

        User user = token.getUser();
        String deviceId = token.getDeviceId();
        // delete old token
        refreshTokenRepository.delete(token);
        // create new token
        return generateRefreshToken(user.getId(), deviceId);
    }

    public String deleteByToken(String token) {

        return refreshTokenRepository.findByRefreshToken(token)
                .map(refreshToken -> {
                    refreshTokenRepository.delete(refreshToken);
                    return "Logged out successfully!";
                })
                .orElse("Invalid refresh token!");
    }

    public int deleteAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return refreshTokenRepository.deleteByUser(user);
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token, String username) {
        String username1 = getUsername(token);
        return username1.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}