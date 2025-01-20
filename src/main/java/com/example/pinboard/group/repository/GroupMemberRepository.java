package com.example.pinboard.group.repository;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.domain.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberModel, Long> {
    boolean existsByUserAndGroup(UserModel user, GroupModel group);

    List<GroupMemberModel> findAllByGroup(GroupModel group);

    List<GroupMemberModel> findByUser(UserModel user);
    List<GroupMemberModel> findByGroup_GroupId(Long groupId);

    Optional<GroupMemberModel> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
