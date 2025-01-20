package com.example.pinboard.memo.repository;

import com.example.pinboard.memo.domain.model.MemoVisibilityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoVisibilityRepository extends JpaRepository<MemoVisibilityModel, Long> {
}
