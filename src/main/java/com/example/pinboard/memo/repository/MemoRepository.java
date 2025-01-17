package com.example.pinboard.memo.repository;

import com.example.pinboard.account.domain.dto.AccountDto;
import com.example.pinboard.memo.domain.dto.CreateMemoDto;
import com.example.pinboard.memo.domain.model.MemoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<MemoModel, Long> {
}
