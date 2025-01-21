package com.example.pinboard.memo.repository;

import com.example.pinboard.memo.domain.model.MemoCommentModel;
import com.example.pinboard.memo.domain.model.MemoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoCommentRepository extends JpaRepository<MemoCommentModel, Long> {
    List<MemoCommentModel> findByMemo(MemoModel memo);

    void deleteByMemo(MemoModel memo);
}
