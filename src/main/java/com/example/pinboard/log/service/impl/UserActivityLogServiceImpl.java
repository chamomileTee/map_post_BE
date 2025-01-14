package com.example.pinboard.log.service.impl;

import com.example.pinboard.account.domain.model.UserModel;
import com.example.pinboard.log.domain.model.UserActivityLogModel;
import com.example.pinboard.log.domain.vo.ActivityType;
import com.example.pinboard.log.repository.UserActivityLogRepository;
import com.example.pinboard.log.service.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityLogServiceImpl implements UserActivityLogService {
    private final UserActivityLogRepository userActivityLogRepository;
    private final HttpServletRequest request;

    @Override
    public void logUserActivity(UserModel user, ActivityType activityType) {
        UserActivityLogModel log = UserActivityLogModel.builder()
                .user(user)
                .activityType(activityType)
                .ipAddress(getClientIpAddress())
                .userAgent(request.getHeader("User-Agent"))
                .build();
        userActivityLogRepository.save(log);
    }

    private String getClientIpAddress() {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0];
    }
}