package com.example.pinboard.group.repository;

import com.example.pinboard.group.domain.model.GroupMemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * GroupMemberRepository
 * <p>그룹 멤버 데이터베이스 접근 레포지토리</p>
 *
 * @author JaeSeung Lee
 * @version 1.0
 * @since 2025-01-13
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberModel, Long> {
}
