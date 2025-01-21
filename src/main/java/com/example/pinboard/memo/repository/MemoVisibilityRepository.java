package com.example.pinboard.memo.repository;

import com.example.pinboard.memo.domain.model.MemoVisibilityModel;
import com.example.pinboard.memo.domain.model.MemoModel;
import com.example.pinboard.account.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoVisibilityRepository extends JpaRepository<MemoVisibilityModel, Long> {
    Optional<MemoVisibilityModel> findByMemoAndUser(MemoModel memo, UserModel user);

    void deleteByMemo(MemoModel memo);
}
