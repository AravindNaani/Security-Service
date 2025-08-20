package com.ecom.Security_Service.service.impl;

import com.ecom.Security_Service.config.JwtConfig;
import com.ecom.Security_Service.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    private JwtConfig jwtConfig;

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex){
            return false;
        }
    }

    @Override
    public String getValidationError(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token);
            return null;
        }catch (ExpiredJwtException e){
            return "EXPIRED_TOKEN";
        }catch (UnsupportedJwtException e){
            return "UNSUPPORTED_TOKEN";
        }catch (MalformedJwtException e){
            return "MALFORMED_TOKEN";
        }catch (SignatureException e){
            return "INVALID_SIGNATURE";
        }catch (IllegalArgumentException e){
            return "EMPTY_OR_NULL_TOKEN";
        }
    }


}
