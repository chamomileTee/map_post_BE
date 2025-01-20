package com.example.pinboard.group.repository;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.domain.model.GroupModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberModel, Long> {
    boolean existsByUserAndGroup(UserModel user, GroupModel group);
    boolean existsByUserAndGroupGroupId(UserModel user, Long groupId);
    GroupMemberModel findByUserAndGroup(UserModel user, GroupModel group);

    List<GroupMemberModel> findAllByGroup(GroupModel group);

<<<<<<< HEAD
    List<GroupMemberModel> findByUser(UserModel user);

=======
>>>>>>> 99dd33955e5fc3c63a23bb800f55717db0800b6e
    Page<GroupMemberModel> findByUser(UserModel user, Pageable pageable);
    List<GroupMemberModel> findByGroup_GroupId(Long groupId);

    Optional<GroupMemberModel> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
