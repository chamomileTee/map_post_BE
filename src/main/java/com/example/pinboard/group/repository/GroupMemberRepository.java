package com.example.pinboard.group.repository;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.group.domain.model.GroupMemberModel;
import com.example.pinboard.group.domain.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberModel, Long> {
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMemberModel gm WHERE gm.user = :user AND gm.group = :group")
    boolean existsByUserAndGroup(@Param("user") UserModel user, @Param("group") GroupModel group);
}
