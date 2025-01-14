package com.example.pinboard.log.service;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.log.domain.vo.ActivityType;

public interface UserActivityLogService {
    void logUserActivity(UserModel user, ActivityType activityType);
}