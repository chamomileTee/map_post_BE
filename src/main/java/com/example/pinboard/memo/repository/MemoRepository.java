package com.example.pinboard.memo.repository;

import com.example.pinboard.memo.domain.model.MemoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<MemoModel, Long> {
}
