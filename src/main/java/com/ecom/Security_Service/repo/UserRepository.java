package com.ecom.Security_Service.repo;

import com.ecom.Security_Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);
}
