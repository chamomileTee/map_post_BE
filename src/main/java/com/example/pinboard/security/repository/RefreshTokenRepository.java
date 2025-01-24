package com.example.pinboard.security.repository;

import com.example.pinboard.security.domain.model.RefreshTokenModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenModel, String> {
    Optional<RefreshTokenModel> findByToken(String token);
    Optional<RefreshTokenModel> findByUserEmail(String userEmail);
}