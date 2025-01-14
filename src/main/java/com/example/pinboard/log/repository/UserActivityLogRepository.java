package com.example.pinboard.log.repository;

import com.example.pinboard.log.domain.model.UserActivityLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLogModel, Long> {
}
