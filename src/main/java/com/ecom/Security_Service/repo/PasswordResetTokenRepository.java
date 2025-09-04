package com.ecom.Security_Service.repo;

import com.ecom.Security_Service.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    void deleteByUserId(Long userId);

    Optional<PasswordResetToken> findByToken(String token);



}
