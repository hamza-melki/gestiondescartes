package com.securityEcommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.securityEcommerce.models.RefreshToken;
import com.securityEcommerce.models.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Query("SELECT r.user.id FROM refreshtoken r WHERE r.token = :token")
  Long findUserIdByToken(@Param("token") String token);

  @Modifying
  int deleteByUser(User user);
}
