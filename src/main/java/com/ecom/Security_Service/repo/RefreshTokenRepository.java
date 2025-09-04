package com.ecom.Security_Service.repo;

import com.ecom.Security_Service.entity.RefreshToken;
import com.ecom.Security_Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findByUserId(Long userId);

    void deleteByRefreshToken(String refreshToken);

    int deleteByUser(User user);

    void deleteAllByUserId(Long userId);
}
