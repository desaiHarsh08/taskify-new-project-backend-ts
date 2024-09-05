package com.taskify.repositories;

import com.taskify.models.RefreshTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {

    Optional<RefreshTokenModel> findByEmail(String email);

    Optional<RefreshTokenModel> findByRefreshToken(String refreshToken);

}
