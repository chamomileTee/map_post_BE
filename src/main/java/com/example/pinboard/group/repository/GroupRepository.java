package com.example.pinboard.group.repository;

import com.example.pinboard.group.domain.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * GroupRepository
 * <p>그룹 데이터베이스 접근 레포지토리</p>
 *
 * @author JaeSeung Lee
 * @version 1.0
 * @since 2025-01-13
 */
@Repository
public interface GroupRepository extends JpaRepository<GroupModel, Long> {
}
