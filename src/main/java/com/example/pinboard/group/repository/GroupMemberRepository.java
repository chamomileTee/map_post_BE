package com.example.pinboard.group.repository;

import com.example.pinboard.group.domain.model.GroupMemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberModel, Long> {
}
