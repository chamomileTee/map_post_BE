package com.example.pinboard.memo.repository;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.memo.domain.model.MemoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<MemoModel, Long> {
    Page<MemoModel> findByUser(UserModel user, Pageable pageable);
}
