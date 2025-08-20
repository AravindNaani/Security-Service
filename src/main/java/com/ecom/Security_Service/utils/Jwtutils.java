package com.ecom.Security_Service.utils;

import com.ecom.Security_Service.config.JwtConfig;
import com.ecom.Security_Service.dto.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class Jwtutils {

    @Autowired
    private JwtConfig jwtConfig;

    public String generateToken(LoginRequest loginRequest){

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("roles",loginRequest.getRoles());

        Date now = new Date();
        Date expiryTime = new Date(now.getTime() + jwtConfig.getExpirationMs());

        return Jwts.builder()
                .setSubject(loginRequest.getUserName())
                .setClaims(roleMap)
                .setIssuedAt(new Date())
                .setExpiration(expiryTime)
                .signWith(SignatureAlgorithm.HS512,jwtConfig.getSecret())
                .compact();
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    private boolean isTokenExpired(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtConfig.getSecret())
                .getBody()
                .getExpiration().before(new Date());
    }

    public boolean validateToken(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtConfig.getSecret())
                    .getBody();

            String subject = claims.getSubject();
            return subject != null && isTokenExpired(jwtConfig.getSecret());
        }catch (IllegalArgumentException | JwtException ex){
            ex.getMessage();
            return false;
        }
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> extractRoles(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles",List.class);
    }
}
